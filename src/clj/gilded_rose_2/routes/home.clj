(ns gilded-rose-2.routes.home
  (:require
   [gilded-rose-2.layout :as layout]
   [gilded-rose-2.inventory :refer [update-inventory initial-inventory]]
   [gilded-rose-2.data-store :refer [data-store]]
   [clojure.java.io :as io]
   [gilded-rose-2.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request] 
  (layout/render request "home.html"))

(defn inventory [_]
  {:body (:inventory @data-store)})

(defn increment-day! [_]
  {:body (do (swap! data-store update :inventory update-inventory)
             (:inventory @data-store))})

(defn reset-inventory! [_]
  {:body (do
           (swap! data-store assoc :inventory initial-inventory)
           (:inventory @data-store))})

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/api"
    ["/inventory" {:get inventory}]
    ["/increment-day" {:get increment-day! }]
    ["/reset-inventory" {:get reset-inventory!}]]])

(comment
  (def example-request (atom {}))
  (let [request @example-request]
    (layout/render request "home.html")))