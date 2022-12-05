(ns gilded-rose-2.routes.home
  (:require
   [gilded-rose-2.layout :as layout]
   [clojure.java.io :as io]
   [gilded-rose-2.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request] 
  (layout/render request "home.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]])

(comment
  (def example-request (atom {}))
  (let [request @example-request]
    (layout/render request "home.html")))