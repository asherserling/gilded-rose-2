(ns gilded-rose-2.routes.home
  (:require
   [gilded-rose-2.layout :as layout]
   [gilded-rose-2.inventory :refer [update-inventory initial-inventory]]
   [gilded-rose-2.data-store :refer [data-store]]
   [clojure.java.io :as io]
   [gilded-rose-2.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [clojure.pprint :as pprint]
   [gilded-rose-2.inventory :as inventory]))

(def make-fresh-inventory
  (let [current-id (atom 0)]
    (fn []
      (map #(assoc % :id (swap! current-id inc)) 
           initial-inventory))))

(defn refresh-inventory! []
  (swap! data-store assoc :inventory (make-fresh-inventory)))

(defn home-page [request] 
  (layout/render request "home.html"))

(defn inventory [_]
  (when (not (contains? @data-store :inventory))
    (refresh-inventory!))
  {:body (:inventory @data-store)})

(defn increment-day! [_]
  {:body (-> (swap! data-store update :inventory update-inventory) 
             :inventory)})

(defn reset-inventory! [_]
  {:body (do
           (swap! data-store assoc :inventory (make-fresh-inventory))
           (:inventory @data-store))})

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/api"
    ["/inventory" {:get inventory}]
    ["/increment-day" {:get increment-day!}]
    ["/reset-inventory" {:get reset-inventory!}]]])

(comment
  (def initial-game-state
    {:turn {:player :1
            :state :to-roll}

     :players {:1 {:id :1 :position 0 :balance 20}
               :2 {:id :2 :position 0 :balance 20}}

     :board (->> [{:name "Go"}
                  {:name "Park Place" :price 5}
                  {:name "Boardwalk" :price 5}
                  {:name "Aquarium" :price 4}
                  {:name "Zoo" :price 4} ;; this is position 4 btw
                  {:name "Teddy's Pizza" :price 3}
                  {:name "Wok In The Park" :price 3}
                  {:name "Acme" :price 2}
                  {:name "Kosher Connection" :price 2}
                  {:name "The Duck Pond" :price 1}
                  {:name "Quick Check" :price 1}]
                 (map-indexed vector)
                 (map (fn [[idx val]] (assoc val :position idx)))
                 (into []))})
  
  (def board-length 10)
  
  (defn current-player [game-state]
    (let [player-id (get-in game-state [:turn :player])]
      (get-in game-state [:players player-id]))) 

  (defn current-square [game-state]
    (get-in game-state [:board (:position (current-player game-state))])) 
  
  (defn update-position [game-state spaces]
    (update-in game-state 
               [:players (:id (current-player game-state)) :position]
               #(mod (+ % spaces) board-length)))
  
  (defn move-player [game-state spaces]
    (update-position game-state spaces)) 
  
  (defn passed-go? [game-state spaces]
    (let [current-player-id (:id (current-player game-state))
          original-position (get-in game-state [:players current-player-id :position])]
      (> (+ spaces original-position) 10))) 
  
  (defn next-player [current-player-id]
    (let [players [:1 :2]
          idx-current-player (.indexOf players current-player-id)]
      (nth players (mod (inc idx-current-player) (count players))))) 
  
  (defn switch-turn [game-state]
    (let [player-path [:turn :player]]
      (-> game-state
          (assoc-in player-path (next-player (get-in game-state player-path)))
          (assoc-in [:turn :state] :to-roll)))) 
  
  (defn current-player-is-owner? [game-state]
    (= (:id (current-player game-state)) (:owned-by (current-square game-state)))) 
  
  (defn owned-by-another-player? [game-state]
    (not (or (nil? (:owned-by (current-square game-state)))
             (current-player-is-owner? game-state)))) 
  
  (defn current-player-cant-afford-space? [game-state]
    (> (or (:price (current-square game-state))
           0)
       (:balance (current-player game-state))))
  
  (defn is-go? [game-state]
    (= 0 (:position (current-player game-state)))) 
  
  (defn lose-current-player [game-state]
    (assoc game-state 
           :winner
           (next-player (:id (current-player game-state)))))
  
  (defn roll-dice []
    (rand-nth (range 1 7)))
  
  (defn move-game-forward [game-state spaces]
    (let [next-pos-game-state (cond-> (move-player game-state spaces)
                                (passed-go? game-state spaces)
                                (update-in [:players (:id (current-player game-state)) :balance] + 2))
          curr-square (current-square next-pos-game-state)]
      (cond

        (or (is-go? next-pos-game-state)
            (current-player-is-owner? next-pos-game-state))
        (switch-turn next-pos-game-state)

        (owned-by-another-player? next-pos-game-state)
        (if (current-player-cant-afford-space? next-pos-game-state)
          (lose-current-player next-pos-game-state)
          (-> next-pos-game-state
              (update-in [:players (:owned-by curr-square) :balance] + (:price curr-square))
              (update-in [:players (:id (current-player next-pos-game-state)) :balance] - (:price curr-square))
              switch-turn))

        :else
        (if (current-player-cant-afford-space? next-pos-game-state)
          (switch-turn next-pos-game-state)
          (assoc-in next-pos-game-state [:turn :state] :to-choose)))))

  (defn apply-action [game-state action]
    (let [turn-state (get-in game-state [:turn :state])]
      (case turn-state
        :to-roll (if (not (= action :roll))
                   game-state
                   (move-game-forward game-state (roll-dice)))
        :to-choose (if (not (some #(= % action) [:buy :pass]))
                     game-state
                     (-> (case action
                           :buy (let [price (:price (current-square game-state))
                                      current-player-id (:id (current-player game-state))
                                      current-square-position (:position (current-square game-state))]
                                  (-> game-state
                                      (update-in [:players current-player-id :balance] - price)
                                      (assoc-in [:board current-square-position :owned-by] current-player-id)
                                      (switch-turn)))
                           :pass (switch-turn game-state)))))))
  
  (def game-state (atom initial-game-state))
  
  (swap! game-state apply-action :roll)
  (swap! game-state apply-action :buy)
  (swap! game-state apply-action :pass)
  
  )

  