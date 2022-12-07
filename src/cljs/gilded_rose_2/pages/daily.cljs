(ns gilded-rose-2.pages.daily
  (:require [re-frame.core :as rf] 
            [gilded-rose-2.api.api :as api]
            [gilded-rose-2.inventory :as inventory]))

(defn make-load-button-attributes [k]
  {:on-click #(rf/dispatch [k])
   :class (when (:is-loading @(rf/subscribe [k])) :is-loading)})

(defn daily-thing [] 
  [:div.columns 
   
   [:div.column.is-one-half.mx-auto
    [:div.box
     [:div.is-flex.is-justify-content-end.pb-4
      [:button.button.is-primary.mr-2
       (make-load-button-attributes ::api/increment-day) 
       "Increment Day"]
      [:button.button.is-warning
       (make-load-button-attributes ::api/reset-inventory)
       "Reset"]] 
     
     [:div.is-flex.is-justify-content-center.pb-6
      {:style {:font-size "1.4em"}}
      (let [inventory @(rf/subscribe [::api/inventory])]
        [:table.table.is-bordered.is-hoverable.is-striped
         [:thead
          [:tr
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
               [:td name] [:td quality] [:td sell-in]]))]])]]]])