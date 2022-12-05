(ns gilded-rose-2.helpers)

(defn format-dollars [amount]
  (let [formatter (.NumberFormat js/Intl. "en-Us" {:style "currency" :currency "USD"})]
    (.format formatter amount)))