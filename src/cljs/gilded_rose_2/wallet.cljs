(ns gilded-rose-2.wallet
  (:require [re-frame.core :as re-frame]))

(def initial-wallet 0)

(re-frame/reg-event-db
 ::init-wallet
 (fn [db _]
   (when-not (::wallet db)
    (assoc db ::wallet initial-wallet))))

(re-frame/reg-event-db
 ::add-money
 (fn [db [_ amount]]
   (update db ::wallet + amount)))

(re-frame/reg-event-db
 ::refresh
 (fn [db _]
   (assoc db ::wallet initial-wallet)))

(re-frame/reg-sub
 ::wallet
 (fn [db _]
   (::wallet db)))