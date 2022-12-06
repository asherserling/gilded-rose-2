(ns gilded-rose-2.pages.store
  (:require [re-frame.core :as rf]
            [gilded-rose-2.notifications :as notifications]
            [gilded-rose-2.inventory :as inventory]
            [gilded-rose-2.transactions :as transactions]
            [gilded-rose-2.wallet :as wallet]
            [gilded-rose-2.helpers :refer [format-dollars]]))

(declare inventory
         inventory-table
         ballance
         make-action-button
         supplier-inventory
         seller-inventory)

(defn store []
  [:div.columns
   [:div.column.is-one-quarter.has-text-centered

    (let [balance @(rf/subscribe [::wallet/wallet])]
      [:div.card.has-background-white-bis
       [:div.card-header
        [:div.card-header-title
         "Balance"]
        [:div.card-header-icon
         [:span.icon
          [:i.fas.fa-dollar-sign]]]]
       [:div.card-content.has-background-light
        [:p.is-size-2
         (str "$" (format-dollars balance))]]])

    [:div.mt-5
     [notifications/notifications]]]

   [:div.column.is-three-quarters
    [:div.columns
     [:div.column.is-one-half
      [:div.box.has-background-white-bis
       {:style {:min-height "500px"}}
       [seller-inventory]]]
     [:div.column.is-one-half
      [:div.box.has-background-white-bis
       {:style {:min-height "500px"}}
       [supplier-inventory]]]]]])

(defn ballance []
  (let [balance @(rf/subscribe [::wallet/wallet])]
    [:div
     [:h1.title
      "Balance"]
     [:p.is-size-2
      (str "$" balance)]]))

(defn inventory [title subscription action-button]
  (let [items @(rf/subscribe [subscription])]
    [:div
     [:div
      {:style {:display "flex"
               :justify-content "center"
               :padding-bottom "15px"}}
      [:h1.title title]]
     [:div.container
      [inventory-table items action-button]]]))

(defn inventory-table [items action-button]
  (let [headers ["Name" "Quality" "Sell In" ""]
        map-el (fn [el vals]
                 (map #(vector el {:key %} %) vals))]
    [:div.container.is-flex.is-justify-content-center
     [:div.table-container
      [:table.table.is-bordered.is-hoverable.is-striped
       [:thead
        [:tr
         (map-el :th headers)]]
       [:tbody
        (if (= 0 (count items))
          [:tr
           [:td {:col-span (count headers)} "Sorry, we're all sold out"]]
          (for [{:keys [name quality sell-in id]} items]
            [:tr {:key id}
             (map-el :td [name quality sell-in [(make-action-button action-button) id]])]))]]]]))

(defn seller-inventory []
  (inventory "Inventory" ::inventory/inventory {:text "Buy"
                                                :action ::transactions/sell-item
                                                :color "is-primary"}))

(defn supplier-inventory []
  (inventory "Supplier Inventory" ::inventory/supplier-inventory {:text "Sell"
                                                                  :action ::transactions/buy-item
                                                                  :color "is-secondary"}))

(defn make-action-button [{:keys [text action color]}]
  (fn [item-id]
    [(keyword (str "button.button." color))
     {:on-click #(rf/dispatch [action item-id])}
     text]))