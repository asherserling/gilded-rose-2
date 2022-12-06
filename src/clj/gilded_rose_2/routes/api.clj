(ns gilded-rose-2.routes.api
  (:require
   [gilded-rose-2.layout :as layout]
   [clojure.java.io :as io]
   [gilded-rose-2.middleware :as middleware] 
   [ring.util.http-response :as response]))

(defn shmendy [_]
  {:status 200 :body "here's your jigga shmendy"})

(defn api-routes []
  ["/api"
   ["/shmendy" {:get shmendy}]])
