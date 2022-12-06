(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
   [gilded-rose-2.config :refer [env]]
    [clojure.pprint]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [gilded-rose-2.core :refer [start-app http-server]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'gilded-rose-2.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'gilded-rose-2.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))
