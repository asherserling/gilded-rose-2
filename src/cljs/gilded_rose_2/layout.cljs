(ns gilded-rose-2.layout
  (:require [clojure.string :as str]
            [goog.string :as gstring]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]
            [gilded-rose-2.transactions :as transactions]
            [gilded-rose-2.helpers :refer [loading-button true-timeout-false]]))

(declare hero nav-bar 
         nav-button)

(defn layout [content]
  [:<>
   [hero] 
   [:div.has-background-primary
    {:style {:padding "0 250px"
             :height "100vh"}}
    [nav-bar]
    [content]]])

(defn hero []
  [:div.hero.has-background-primary.has-text-centered
   [:div.hero-body
    [:div.title.has-text-light
     {:style {:font-size "5em"}}
     (str "The Gilded Rose Store " (gstring/unescapeEntities "&copy;"))]
    [:div.subtitle.has-text-gray
     "Where you get all of your gilded rose things"]]])

(defn nav-bar []
  [:div.box.px-4.py-4.is-flex.is-flex-direction-row.is-justify-content-space-between
   [:div.is-flex.is-flex-direction-row
    [nav-button "Dashboard" :dashboard "has-background-warning-light"]
    [nav-button "Settings" :settings "has-background-primary-light"]
    [nav-button "Daily" :daily "has-background-info-light"]]
   [loading-button "Refresh" #(rf/dispatch [::transactions/refresh]) "has-background-info-light"]])

(defn nav-button [text page background]
  (let [[is-loading set-is-loading] (true-timeout-false)]
   (fn []
     [:a
      {:href (rfe/href page)}
      [(keyword (str "button.button.mr-5." background))
       {:class (str/join " " [(when (= page @(rf/subscribe [:common/page-id])) "is-active")
                              (when @is-loading "is-loading")])
        :on-click set-is-loading}
       text]])))
