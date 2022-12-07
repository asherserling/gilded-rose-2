(ns gilded-rose-2.api.api
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]))
  
  ;; general functions for dealing with getting data from server
  (defn make-fetch-data-effect 
    ([a-keyword uri & other-events]
     (fn [{:keys [db]} _]
       {:http-xhrio {:method          :get
                     :uri             (str "/api/" uri)
                     :response-format (ajax/json-response-format {:keywords? true})
                     :on-success      (into [] (cons ::on-success-fetch (conj other-events a-keyword)))}
        :db (assoc-in db [a-keyword :is-loading] true)})))
  
  (rf/reg-event-fx
   ::on-success-fetch
   (fn [_ [& args]]
     (let [a-keyword (second args)
           the-data (last args)
           other-events (if (> (count args) 3)
                          (subvec (into [] args) 2 (dec (count args)))
                          [])]
       
       {:fx (concat [[:dispatch [::save-data a-keyword the-data]]
                     [:dispatch [::set-loading a-keyword false]]] 
                    (for [event other-events]
                      [:dispatch (into [] (conj event the-data))]))}))) 
  
  (rf/reg-event-db
   ::save-data
   (fn [db [_ a-keyword data]]
     (-> db
         (assoc-in [a-keyword :data] data)
         (assoc-in [a-keyword :is-loading] false))))
  
  (rf/reg-event-db
   ::set-loading
   (fn [db [_ a-keyword a-boolean]]
     (assoc-in db [a-keyword :is-loading] a-boolean)))
  
  ;; ---------------------------
  
  (let [inventory-routes (->> [{:name ::inventory
                                :uri "inventory"}

                               {:name ::increment-day
                                :uri "increment-day"
                                :events [[::save-data ::inventory]]}

                               {:name ::reset-inventory
                                :uri "reset-inventory"
                                :events [[::save-data ::inventory]]}]

                              (map (fn [route]
                                     (if-not (:events route)
                                       (assoc route :events [])
                                       route))))]
    
    (doseq [{:keys [name uri events]} inventory-routes]
      (rf/reg-event-fx
       name
       (apply make-fetch-data-effect (concat [name uri] events)))
      
      (rf/reg-sub
       name
       (fn [db _]
         (name db)))))
  