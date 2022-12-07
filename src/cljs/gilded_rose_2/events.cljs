(ns gilded-rose-2.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
 :common/navigate
 (fn [db [_ match]]
   (let [old-match (:common/route db)
         new-match (assoc match :controllers
                          (rfc/apply-controllers (:controllers old-match) match))]
     (assoc db :common/route new-match))))

(rf/reg-fx
 :common/navigate-fx!
 (fn [[k & [params query]]]
   (rfe/push-state k params query)))

(rf/reg-event-fx
 :common/navigate!
 (fn [_ [_ url-key params query]]
   {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
 :set-docs
 (fn [db [_ docs]]
   (assoc db :docs docs)))

(rf/reg-event-fx
 :fetch-docs
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "/docs"
                 :response-format (ajax/raw-response-format)
                 :on-success       [:set-docs]}}))

(defn create-inventory-load-effect [uri]
  {:http-xhrio {:method          :get
                :uri             uri
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success      [:set-inventory]}})

(rf/reg-event-fx
 :fetch-inventory
 (fn [_ _]
   (create-inventory-load-effect "/api/inventory")))

(rf/reg-event-fx
 :increment-day
 (fn [_ _]
   (create-inventory-load-effect "/api/increment-day")))

(rf/reg-event-fx
 :reset-inventory
 (fn [_ _]
   (create-inventory-load-effect "api/reset-inventory")))


(rf/reg-event-db
 :set-inventory
 (fn [db [_ inventory]]
   (assoc db :inventory-silly inventory)))

(rf/reg-sub
 :inventory-silly
 (fn [db _]
   (:inventory-silly db)))

(comment
  (rf/dispatch [:fetch-docs])

  @(rf/subscribe [:inventory-silly])

  (rf/dispatch [:fetch-inventory])
  (rf/dispatch [:increment-day])
  (map :occupation @(rf/subscribe [:inventory-silly]))
  )

(rf/reg-event-db
 :common/set-error
 (fn [db [_ error]]
   (assoc db :common/error error)))

(rf/reg-event-fx
 :page/init-home
 (fn [_ _]
   {:dispatch [:fetch-docs]}))

(rf/reg-event-fx
 :page/init-shmendy
 (fn [_ _]
   {:dispatch [:fetch-shmendy]}))

;;subscriptions

(rf/reg-sub
 :common/route
 (fn [db _]
   (-> db :common/route)))

(rf/reg-sub
 :common/page-id
 :<- [:common/route]
 (fn [route _]
   (-> route :data :name)))

(rf/reg-sub
 :common/page
 :<- [:common/route]
 (fn [route _]
   (-> route :data :view)))

(rf/reg-sub
 :common/error
 (fn [db _]
   (:common/error db)))
