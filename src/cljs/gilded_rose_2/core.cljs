(ns gilded-rose-2.core
  (:require
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [gilded-rose-2.ajax :as ajax]
   [gilded-rose-2.settings :as settings]
   [gilded-rose-2.layout :refer [layout]]
   [gilded-rose-2.store :refer [store]]
   [gilded-rose-2.transactions :as transactions]
   [gilded-rose-2.events]
   [gilded-rose-2.pages.settings :refer [settings]]
   [reitit.core :as reitit]
   [reitit.frontend.easy :as rfe]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType])
  (:import goog.History))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
   [["/" {:name :dashboard
          :view #'store
          :controllers [{:start #(rf/dispatch [::transactions/init-app])}]}]
    ["settings" {:name :settings
                 :view #'settings}]]))

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
  (mount-components))
