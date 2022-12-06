(ns gilded-rose-2.pages.settings
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [gilded-rose-2.notifications :as notifications]
            [gilded-rose-2.settings :as s]))

(declare input-number validate-state)

(defn settings []
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
      (let [initial-state @(rf/subscribe [::s/settings])
            form-state (r/atom initial-state)
            errors (r/atom (validate-state initial-state))]
        [:form
         [:<>
          [input-number "Seller Rate" :seller-rate form-state errors]
          [input-number "Supplier Rate" :supplier-rate form-state errors]]

         [:div.is-flex.is-justify-content-end.pt-3
          [:button.button.is-info
           {:on-click (fn [e]
                        (when (and  (not= initial-state @form-state) (= 0 (count @errors)))
                          (.preventDefault e)
                          (doseq [[key val] @form-state]
                            (rf/dispatch [::s/set-key key val]))
                          (rf/dispatch [::notifications/add-notification :success "Settings updated"])))}
           "Save"]]])]]]])

(defn input-number [label key form-state errors]
  [:div.field
   [:div.is-flex.is-flex-row.is-justify-content-space-between
    [:label.label label]
    (when (key @errors) [:div.tag.is-danger.is-light (key @errors)])]
   [:div.control
    [:input.input
     {:type "number"
      :required true
      :value (key @form-state)
      :on-change (fn [e]
                   (.preventDefault e)
                   (swap! form-state assoc key (-> e .-target .-value))
                   (reset! errors (validate-state @form-state)))}]]])

(defn validate-state [form-state]
  (cond-> {}
    (<= (:supplier-rate form-state) 0) 
    (assoc :supplier-rate "Supplier rate must be greater than zero")
    
    (<= (:seller-rate   form-state) 0)
    (assoc :seller-rate   "Seller rate must be greater than zero")))