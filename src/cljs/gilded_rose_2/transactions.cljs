(ns gilded-rose-2.transactions
  (:require [gilded-rose-2.inventory :as inventory]
            [gilded-rose-2.wallet :as wallet]
            [gilded-rose-2.notifications :as notifications]
            [re-frame.core :as re-frame]
            [gilded-rose-2.settings :as settings]
            [gilded-rose-2.helpers :refer [format-dollars]]))

(defn get-rate-item [item seller]
  (let [rate-key (condp = seller
                   :supplier :supplier-rate
                   :me :seller-rate)]
    (settings/get-key rate-key)))

(defn evaluate-item [item seller]
  (* (get-rate-item item seller) (:quality item)))

(defn make-sale-base-message [item price rate]
  (str
   "a " (:name item)
   " at a rate of " (format-dollars rate) " dollars for"
   " each quality unit for a total of"
   " " (format-dollars price) " dollars"))

(re-frame/reg-event-fx
 ::sell-item
 (fn [_ [_ item-id]]
   (let [item (inventory/get-item ::inventory/inventory item-id) 
         price (evaluate-item item :me)
         rate (get-rate-item item :me)]
     {:fx [[:dispatch [::inventory/remove-item ::inventory/inventory item-id]]
           [:dispatch [::wallet/add-money price]]
           [:dispatch [::notifications/add-notification :success (str "Sold " (make-sale-base-message item
                                                                                                      price
                                                                                                      rate))]]
           [:dispatch [:add-history (str "Sold " item)]]]})))

(re-frame/reg-event-fx
 ::buy-item
 (fn [_ [_ item-id]]
   (let [item (inventory/get-item ::inventory/supplier-inventory item-id)
         price (evaluate-item item :supplier)
         rate (get-rate-item item :supplier)
         balance @(re-frame/subscribe [::wallet/wallet])
         suficient-funds (>= balance price)]
     {:fx (cond 
            suficient-funds
            [[:dispatch [::inventory/remove-item ::inventory/supplier-inventory item-id]]
             [:dispatch [::inventory/add-item ::inventory/inventory item]]
             [:dispatch [::wallet/add-money (* -1 price)]]
             [:dispatch [::notifications/add-notification :info (str "Bought " (make-sale-base-message item
                                                                                                     price
                                                                                                     rate))]] 
             [:dispatch [:add-history (str "Failed to buy " item)]]]
            
            
            :else
            [[:dispatch [::notifications/add-notification :error (str "Can not buy a " (:name item)
                                                                      " for $" price ". Insufficent funds")]]
             [:dispatch [:add-history (str "Bought item" item)]]])})))

(re-frame/reg-event-fx
 ::init-app
 (fn [_ _]
   (if (not @(re-frame/subscribe [::inventory/inventory]))
     {:fx [[:dispatch [::refresh]]]}
     {})))

(re-frame/reg-event-fx
 ::refresh
 (fn [_ _]
   {:fx [[:dispatch [::inventory/init-inventory ::inventory/inventory]]
         [:dispatch [::inventory/init-inventory ::inventory/supplier-inventory]]
         [:dispatch [::wallet/refresh]]
         [:dispatch [::notifications/refresh]]
         [:dispatch [:refresh-history]]
         [:dispatch [::settings/refresh]]]}))

(re-frame/reg-event-db
 :add-history
 (fn [db [_ item]]
   (update db :history conj item)))

(re-frame/reg-event-db
 :refresh-history
 (fn [db _]
   (assoc db :history [])))

(re-frame/reg-sub
 :history
 (fn [db _]
   (:history db)))