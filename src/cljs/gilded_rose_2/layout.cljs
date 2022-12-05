(ns gilded-rose-2.layout
  (:require [goog.string :as gstring]
            [re-frame.core :as rf]
            [reitit.frontend.easy :as rfe]
            [gilded-rose-2.transactions :as transactions]))

(declare nav-button)

(defn layout [content]
  [:<>
   [:div.hero.has-background-primary.has-text-centered
    [:div.hero-body
     [:div.title.has-text-light
      {:style {:font-size "5em"}}
      (str "The Gilded Rose Store " (gstring/unescapeEntities "&copy;"))]
     [:div.subtitle.has-text-gray
      "Where you get all of your gilded rose things"]]]

   [:div.has-background-primary
    {:style {:padding "0 150px"
             :height "100vh"}}
    [:div.px-6.pt-6
     {:style {:height "100vh"
              :border-radius "7px"}}

     [:div.box.px-4.py-4.is-flex.is-flex-direction-row.is-justify-content-space-between
      [:div.is-flex.is-flex-direction-row
       [nav-button (rfe/href :dashboard) "Dashboard" :dashboard "has-background-warning-light"]
       [nav-button (rfe/href :settings) "Settings" :settings "has-background-primary-light"]]

      [:button.button.has-background-info-light
       {:on-click #(rf/dispatch [::transactions/refresh])}
       "Refresh"]]

     [content]]]])

(defn nav-button [uri text page background]
  [:a
   {:href uri}
   [(keyword (str "button.button.mr-5." background))
    {:class (when (= page @(rf/subscribe [:common/page-id])) :is-active)}
    text]])