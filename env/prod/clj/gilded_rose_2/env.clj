(ns gilded-rose-2.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[gilded-rose-2 started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[gilded-rose-2 has shut down successfully]=-"))
   :middleware identity})
