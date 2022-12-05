(ns gilded-rose-2.routes.sandbox
  (:require [re-frame.core :as re-frame]
            [gilded-rose-2.events :as events] 
            [gilded-rose-2.inventory :as inventory]
            [gilded-rose-2.wallet :as wallet]
            [gilded-rose-2.transactions :as transactions]
            [gilded-rose-2.notifications :as notifications]
            [gilded-rose-2.settings :as settings]
            [re-frame.core :as rf]))


(comment
  
  (re-frame/dispatch [::settings/init])
  @(re-frame/subscribe [::settings/settings])
  @(re-frame/subscribe [::settings/get-key :supplier-rate])
  (settings/get-key :supplier-rate)
  
  (:supplier-rate @(re-frame/subscribe [::settings/settings]))
  
  (re-frame/dispatch [::settings/set-key :supplier-rate 2])
  
  (re-frame/dispatch [::events/refresh-db]) 
  (re-frame/dispatch [::events/add-to-wallet 20])
  (re-frame/dispatch [::events/init-wallet])
  (re-frame/dispatch [::inventory/init-inventory])
  (re-frame/dispatch [::inventory/silly-add "third"])
  (re-frame/dispatch [::wallet/init-wallet])
  
  (re-frame/dispatch [::notifications/add-notification :success "success!"])
  @(re-frame/subscribe [::notifications/notifications])
  (re-frame/dispatch [::notifications/remove-notification 1])
  
  (let [item-id (-> @(re-frame/subscribe [::inventory/inventory])
                    (nth 0)
                    :id)]
    (re-frame/dispatch [::inventory/remove-item item-id]))
  
  (re-frame/dispatch [::event/refresh-db])
  
  (let [db @(re-frame/subscribe [::subs/db])
        item (-> @(re-frame/subscribe [::inventory/inventory])
                 (nth 0)
                 :id)]
    (update db ::inventory/inventory (fn [inventory]
                                       (filter #(not (= (:id %) item)) inventory)))
    db)
  
  (count @(re-frame/subscribe [::inventory/inventory]))
  
  
  (re-frame/dispatch [::transactions/sell-item "b"])
  
  (re-frame/dispatch [::wallet/init-wallet])
  (re-frame/dispatch [::wallet/refresh])
  (re-frame/dispatch [::wallet/add-money -60])
  @(re-frame/subscribe [::wallet/wallet])
  
  (re-frame/dispatch [::inventory/init-inventory])
  @(re-frame/subscribe [::inventory/get-item "a"])
  (count @(re-frame/subscribe [::inventory/inventory]))
  
  (::wallet/wallet @(re-frame/subscribe [::subs/db]))
   
  (re-frame/dispatch [::inventory/init-supplier-inventory])
  @(re-frame/subscribe [::inventory/supplier-inventory])
  )