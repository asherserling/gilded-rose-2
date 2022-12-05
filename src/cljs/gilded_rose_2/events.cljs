(ns gilded-rose-2.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

;; db
(rf/reg-event-db
 ::refresh-db
 (fn [_ _]
   {}))

(rf/reg-event-db
 ::init-wallet
 (fn [db _]
   (assoc db ::wallet 100)))

(rf/reg-event-db
 ::add-to-wallet
 (fn [db [_ amount]]
   (update db ::wallet + amount)))

(defn get-item-by-id [db id]
  (->> db
       :inventory 
       (filter #(= (:id %) id))
       first))


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

(rf/reg-event-db
 :set-shmendy
 (fn [db [_ shmendy]]
   (assoc db :shmendy shmendy)))

(rf/reg-event-fx
 :fetch-docs
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "/docs"
                 :response-format (ajax/raw-response-format)
                 :on-success       [:set-docs]}}))


(rf/reg-event-fx
 :fetch-shmendy
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "/api/shmendy"
                 :response-format (ajax/raw-response-format)
                 :on-success      [:set-shmendy]}}))

(rf/reg-event-db
 ::log-form
 (fn [db [_ new-val]]
   (update-in db [:forms] conj new-val)))

(rf/reg-event-db
 ::init-inventory
 (fn [db _]
   (assoc db ::inventory [{:name "Sulfuras" :quality 2 :sell-in 10 :id "a"}
                         {:name "Conjured" :quality 4 :sell-in 6 :id "b"}
                         {:name "Lizzi" :quality 100 :sell-in "never"}])))

(rf/reg-event-db
 ::remove-item-inventory
 (fn [db [_ item-id]]
   (update-in db [::inventory] 
              (fn [inventory]
                (filter #(not (= item-id (:id %))) inventory)))))

(rf/dispatch [::init-inventory])
(rf/reg-sub
 ::inventory
 (fn [db]
   (:inventory db)))
@(rf/subscribe [::inventory])

(comment)

(comment
  (let [my-map {:house {:rooms 2}}]
    (update-in my-map [:house :rooms] inc)))

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
 :docs
 (fn [db _]
   (:docs db)))

(rf/reg-sub
 :shmendy
 (fn [db _]
   (:shmendy db)))

(rf/reg-sub
 :common/error
 (fn [db _]
   (:common/error db)))
