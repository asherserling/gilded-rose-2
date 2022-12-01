(ns gilded-rose-2.app
  (:require [gilded-rose-2.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
