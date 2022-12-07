(ns gilded-rose-2.core
  (:require
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [gilded-rose-2.ajax :as ajax]
   [day8.re-frame.http-fx]
   [gilded-rose-2.settings :as s]
   [gilded-rose-2.layout :refer [layout]]
   [gilded-rose-2.pages.store :refer [store]]
   [gilded-rose-2.pages.settings :refer [settings]]
   [gilded-rose-2.pages.daily :refer [daily-thing]]
   [gilded-rose-2.transactions :as transactions]
   [gilded-rose-2.api.inventory :as inv]
   [gilded-rose-2.api.api :as api]
   [gilded-rose-2.events] 
   [reitit.core :as reitit]
   [reitit.frontend.easy :as rfe]
   [goog.events :as events]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
   [["/" {:name        :dashboard
          :view        #'store}]
    ["settings" {:name :settings
                 :view #'settings}]
    ["daily" {:name :daily
              :view #'daily-thing
              :controllers [{:start #(rf/dispatch [::api/inventory])}]}]]))

(defn start-router! []
  (rfe/start!
   router
   navigate!
   {}))

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [layout page]))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (rf/dispatch [::transactions/init-app])
  (mount-components))
