(ns gilded-rose-2.notifications
  (:require [re-frame.core :as rf]))

(def generate-id
  (let [current (atom 0)]
    (fn []
      (swap! current inc))))

(rf/reg-event-db
 ::add-notification
 (fn [db [_ type message]]
   (let [id (generate-id)]
     (js/setTimeout #(rf/dispatch [::remove-notification id]) 5000)
     (update db ::notifications conj {:type type
                                      :message message
                                      :id id}))))

(rf/reg-event-db
 ::remove-notification
 (fn [db [_ id]]
   (update db ::notifications (partial filter #(not (= id (:id %)))))))

(rf/reg-event-db
 ::refresh
 (fn [db _]
   (assoc db ::notifications [])))

(rf/reg-sub
 ::notifications
 (fn [db _]
   (::notifications db)))

(defn notifications []
  [:div
   (for [{:keys [type message id]} (sort-by :id > @(rf/subscribe [::notifications]))]
     [:div.box.has-background-white-bis
      {:key id}
      [:div.message
       {:class (type {:success "is-primary"
                      :info "is-info"
                      :error "is-warning"})}
       [:div.message-header
        [:div
         (type {:success "Success!"
                :info "Success!"
                :error "Uh oh!"})]
        [:button.delete
         {:on-click #(rf/dispatch [::remove-notification id])}]]
       [:div.message-body
        message]]])])