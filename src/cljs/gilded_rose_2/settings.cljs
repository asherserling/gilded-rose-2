(ns gilded-rose-2.settings
  (:require [re-frame.core :as rf]))

(def default-settings
  {:supplier-rate 1
   :seller-rate 2})

(rf/reg-event-db
 ::refresh
 (fn [db _]
   (assoc db ::settings default-settings)))

(rf/reg-event-db
 ::init
 (fn [db _]
   (assoc db ::settings default-settings)))

(rf/reg-sub
 ::settings
 (fn [db _]
   (::settings db)))

(rf/reg-event-db
 ::set-key
 (fn [db [_ name value]]
   (assoc-in db [::settings name] value)))

(defn get-key [key]
  (key @(rf/subscribe [::settings])))

(rf/reg-sub
 ::get-key
 (fn [db [_ key]]
   (get-in db [::settings key])))

(rf/reg-event-db
 ::get-by-name
 (fn [db [_ name]]
   (get-in db [::settings name] (atom {}))))


(rf/reg-event-db
 ::unset-settings
 (fn [db _]
   (assoc db ::silly-settings nil)))