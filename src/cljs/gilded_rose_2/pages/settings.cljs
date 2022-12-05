(ns gilded-rose-2.pages.settings
  (:require [re-frame.core :as rf]
            [gilded-rose-2.settings :as s]))

(declare rate-field)

(defn settings []
  [:div.box
   [:div.columns
    [:div.column.is-one-third 
     [rate-field "Supplier Rate" :supplier-rate]
     [rate-field "Seller Rate" :seller-rate]]]])

(defn rate-field [label key]
  [:div.field
   [:label.label label]
   [:div.control
    [:input.input
     {:type "number"
      :value (s/get-key key)
      :on-change #(rf/dispatch [::s/set-key key (-> % .-target .-value)])}]]])

(comment
  (rf/dispatch [::s/set-key :supplier-rate 1])
  (s/get-key :supplier-rate))