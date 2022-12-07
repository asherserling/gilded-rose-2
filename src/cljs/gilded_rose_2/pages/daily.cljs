(ns gilded-rose-2.pages.daily
  (:require [re-frame.core :as rf]
            [gilded-rose-2.pages.store :as store]
            [gilded-rose-2.inventory :as inventory]))

(defn daily-thing []
  [:div.columns
   
   [:div.column.is-one-third.mx-auto
    [:div.box
     [:div.is-flex.is-justify-content-end.pb-4
      [:button.button.is-primary.mr-2
       {:on-click #(rf/dispatch [:increment-day])}
       "Increment Day"]
      [:button.button.is-warning
       {:on-click #(rf/dispatch [:reset-inventory])}
       "Reset"]]
     [:div.is-flex.is-justify-content-space-between

      [store/inventory-table @(rf/subscribe [:inventory-silly])]]]]])