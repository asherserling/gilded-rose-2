(ns gilded-rose-2.pages.daily
  (:require [re-frame.core :as rf]))

(defn daily-thing []
  [:div.box
   [:div.is-flex.is-justify-content-space-between
    [:ul
    (for [item @(rf/subscribe [:inventory-silly])]
      [:li (str item)])]
    
    [:div
     [:button.button.is-primary.mr-2
     {:on-click #(rf/dispatch [:increment-day])}
     "Increment Day"]
     [:button.button.is-warning
      {:on-click #(rf/dispatch [:reset-inventory])}
      "Reset"]]]])