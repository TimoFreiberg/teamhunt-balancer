(ns balance-calc.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce ids (atom 0))

(defonce app-state
  (atom [(atom {:id 1
                :name "test player"
                :balance 25000})]))

(defn player-row [player]
  [:tr
   [:td
    [:input
     {:type "text"
      :alt "Player Name"
      :value (:name @player)
      :on-change (fn update-player-name
                   [e]
                   (swap! player #(assoc % :name (-> e .-target .-value))))}]]
   [:td
    [:input
     {:type "text"
      :value (:balance @player)}]]])

(defn hunt-calc-page []
  [:div
   [:h1 "Team Hunt Balance Calculator"]
   [:table
    [:thead
     [:tr
      [:th "Player"]
      [:th "Balance"]
      [:th "Loot (optional)"]]]
    [:tbody
     (doall
      (for [player @app-state]
        ^{:key (:id @player)}
        [player-row player]))]]])

(reagent/render-component [hunt-calc-page]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
