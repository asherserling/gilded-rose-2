(ns gilded-rose-2.pages.daily
  (:require             [re-frame.core :as rf]
                        [gilded-rose-2.api.api :as api]
                        [gilded-rose-2.helpers :refer [loading-button]]))

(defn daily-thing []
  [:div.columns

   [:div.column.is-one-half.mx-auto
    [:div.box
     
     [:div.is-flex.is-justify-content-end.pb-4
      [:div.mr-2
       [loading-button "Increment Day" #(rf/dispatch [::api/increment-day]) "is-primary"]]
      [:div
       [loading-button "Reset" #(rf/dispatch [::api/reset-inventory]) "is-warning"]]]

     [:div.is-flex.is-justify-content-center.pb-6
      {:style {:font-size "1.4em"}}
      (let [inventory @(rf/subscribe [::api/inventory])]
        [:table.table.is-bordered.is-hoverable.is-striped
         [:thead
          [:tr.is-size-4
           [:th "Name"] [:th "Quality"] [:th "Sell In"]]]

         [:tbody
          (cond
            (:is-loading inventory) [:tr
                                     [:td
                                      {:col-span 3}
                                      "Loading...."]]

            (= 0 (count (:data inventory))) [:tr
                                             [:td
                                              {:col-span 3}
                                              "There are no items in the inventory"]]

            :else
            (for [{:keys [name quality sell-in id]} (:data inventory)]
              [:tr
               {:key id}
               [:td.is-italic.is-flex.is-justify-content-space-between
                [:div.pr-6
                 name]
                [:div
                 (str "  (" id ")")]] 
               [:td.has-text-weight-bold.has-text-right
                quality]
               [:td.has-text-weight-bold.has-text-right 
                sell-in]]))]])]]]])