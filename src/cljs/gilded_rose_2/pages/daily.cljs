(ns gilded-rose-2.pages.daily
  (:require [re-frame.core :as rf]
            [gilded-rose-2.api.inventory :as inv]
            [gilded-rose-2.api.api :as api]))

(defn daily-thing [] 
  [:div.columns 
   
   ;; :class (when (:is-loading @(rf/subscribe [::api/inventory])) :is-loading)
   
   [:div.box
    [:button.button
     {
      :on-click #(rf/dispatch [::api/fetch-inventory])}
     "Click me"]]
   
   [:div.column.is-one-half.mx-auto
    [:div.box
     [:div.is-flex.is-justify-content-end.pb-4
      [:button.button.is-primary.mr-2
       {:on-click #(rf/dispatch [::api/increment-day])}
       "Increment Day"]
      [:button.button.is-warning
       {:on-click #(rf/dispatch [::inv/reset-inventory])}
       "Reset"]] 
     
     [:div.is-flex.is-justify-content-center.pb-6
      {:style {:font-size "1.4em"}}
      (let [inventory-items (:data @(rf/subscribe [::api/inventory]))]
        [:table.table.is-bordered.is-hoverable.is-striped
         [:thead
          [:tr
           [:th "Name"] [:th "Quality"] [:th "Sell In"]]]
         [:tbody
          (if (= 0 (count inventory-items))
            [:tr
             [:td
              {:col-span 3}
              "There are no items in the inventory"]]
            (for [{:keys [name quality sell-in id]} inventory-items]
              [:tr
               {:key id}
               [:td name] [:td quality] [:td sell-in]]))]])]]]])