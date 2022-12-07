(ns gilded-rose-2.helpers
  (:require [reagent.core :as r]))

(defn format-dollars [amount]
  (let [formatter (.NumberFormat js/Intl. "en-Us" {:style "currency" :currency "USD"})]
    (.format formatter amount)))

(defn loading-button [text action color]
  (let [is-loading (r/atom false)]
    (fn []
      [(keyword (str ":button.button." color))
       {:on-click (fn []
                    (action)
                    (reset! is-loading true)
                    (js/setTimeout #(reset! is-loading false) 250))
        :class (when @is-loading :is-loading)}
       text])))