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
        :db (-> db
                (assoc-in [a-keyword :is-loading] true))})))
  
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
  
  (rf/reg-event-fx
   ::inventory
   (make-fetch-data-effect ::inventory "inventory"))
  
  (rf/reg-sub
   ::inventory
   (fn [db _]
     (::inventory db)))
  
  ;; ---------------------------
  
  (rf/reg-event-fx
   ::increment-day
   (make-fetch-data-effect ::increment-day "increment-day" [::save-data ::inventory])) 
  
  (rf/reg-sub
   ::increment-day
   (fn [db _]
    (::increment-day db)))
  
  ;; ---------------------------
  
  (rf/reg-event-fx
   ::reset-inventory
   (make-fetch-data-effect ::reset-inventory "reset-inventory" [::save-data ::inventory]))
  
  (rf/reg-sub
   ::reset-inventory
   (fn [db _]
     (::reset-inventory db)))