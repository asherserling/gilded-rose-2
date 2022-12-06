(ns gilded-rose-2.layout
  (:require [goog.string :as gstring]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]
            [gilded-rose-2.transactions :as transactions]))

(declare hero nav-bar 
         nav-button
         refresh-button)

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
    [nav-button (rfe/href :dashboard) "Dashboard" :dashboard "has-background-warning-light"]
    [nav-button (rfe/href :settings) "Settings" :settings "has-background-primary-light"]]
   [refresh-button]])

(defn nav-button [uri text page background]
  [:a
   {:href uri}
   [(keyword (str "button.button.mr-5." background))
    {:class (when (= page @(rf/subscribe [:common/page-id])) :is-active)}
    text]])

(defn refresh-button []
  (let [loading (r/atom false)]
    (fn []
      [:button.button.has-background-info-light
       {:class (when @loading :is-loading)
        :on-click (fn []
                    (rf/dispatch [::transactions/refresh])
                    (reset! loading true)
                    (js/setTimeout #(reset! loading false) 300))}
       "Refresh"])))
