(ns gilded-rose-2.pages.settings
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [gilded-rose-2.notifications :as notifications]
            [gilded-rose-2.settings :as s]))

(declare rate-field)

(defn settings []
  (let [initial-state @(rf/subscribe [::s/settings])
        form-state (r/atom initial-state)]
    [:div.columns
     [:div.column.is-one-third
      [notifications/notifications]]
     [:div.column.is-one-third
      [:div.card
       [:div.card-header
        [:div.card-header-title "Settings"]
        [:div.card-header-icon
         [:span.icon.fas.fa-gear]]]
       [:div.card.card-content
        [:<>
         [rate-field "Supplier Rate" :supplier-rate form-state]
         [rate-field "Seller Rate" :seller-rate form-state]]
        
        [:div.is-flex.is-justify-content-end.pt-3
         [:button.button.is-info
          {:on-click (fn [] (when (not= @form-state initial-state)
                             (do
                              (doseq [[key val] @form-state]
                                (rf/dispatch [::s/set-key key val]))
                              (rf/dispatch [::notifications/add-notification :success "Settings updated"]))))}
          "Save"]]]]]]))

(defn rate-field [label key form-state]
  [:div.field
   [:label.label label]
   [:div.control
    [:input.input
     {:type "number"
      :value (key @form-state)
      :on-change #(swap! form-state assoc key (-> % .-target .-value))}]]])


(comment
  (rf/dispatch [::s/set-key :supplier-rate 1])
  (s/get-key :supplier-rate)
  temp
  
  (fn [] (for [[key val] @form-state]
           (do
             (.log js/console [key val])
             (rf/dispatch [::s/set-key key val])))) 
  
  )