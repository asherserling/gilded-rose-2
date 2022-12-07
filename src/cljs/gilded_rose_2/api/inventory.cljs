(ns gilded-rose-2.api.inventory
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]))

(defn create-inventory-load-effect
  ([uri]
   {:http-xhrio {:method          :get
                 :uri             uri
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [[:dispatch [::set-inventory]]]}})) 
  
(rf/reg-event-fx
 ::fetch-inventory
 (fn [_ _]
   (create-inventory-load-effect "/api/inventory")))

(rf/reg-event-fx
 ::increment-day
 (fn [_ _]
   (create-inventory-load-effect "/api/increment-day")))

(rf/reg-event-fx
 ::reset-inventory
 (fn [_ _]
   (create-inventory-load-effect "api/reset-inventory")))

(rf/reg-event-db
 ::set-inventory
 (fn [db [_ inventory]]
   (assoc db ::inventory inventory)))

(rf/reg-sub
 ::inventory
 (fn [db _]
   (::inventory db)))