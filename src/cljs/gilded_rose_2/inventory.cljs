(ns gilded-rose-2.inventory
  (:require [re-frame.core :as re-frame]))

(def id-generator
  (let [current (atom 0)]
   (fn []
     (swap! current inc))))

(defn make-initial-inventory []
  (map
   #(assoc % :id (id-generator))
   [{:name "Sulfuras" :quality 2 :sell-in 10}
    {:name "Conjured" :quality 4 :sell-in 6}
    {:name "Lizzi" :quality 100 :sell-in 12}]))

;; events
(re-frame/reg-event-db
 ::init-inventory
 (fn [db [_ inventory-key]]
   (assoc db inventory-key (make-initial-inventory))))

(re-frame/reg-event-db
 ::add-item
 (fn [db [_ inventory-key item]]
   (update db inventory-key conj item)))

(re-frame/reg-event-db
 ::remove-item
 (fn [db [_ inventory-key item-id]]
   (update db inventory-key (partial filter #(not (= (:id %) item-id))))))

;; supscriptions
(re-frame/reg-sub
 ::inventory
 (fn [db _]
   (::inventory db)))

(re-frame/reg-sub
 ::get-item
 (fn [db [_ item-id]]
   (let [inventory (::inventory db)]
    (first (filter #(= (:id %) item-id) inventory)))))

(defn get-item [inventory-key item-id]
  (let [inventory @(re-frame/subscribe [inventory-key])]
    (first (filter #(= (:id %) item-id) inventory))))

(re-frame/reg-sub
 ::supplier-inventory
 (fn [db _]
   (::supplier-inventory db)))
