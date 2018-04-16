(ns balance-calc.core
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.string :as gstring]
            [goog.string.format]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce ids (atom 0))

(defn get-new-id []
  (let [id @ids
        _ (swap! ids inc)]
    id))

(defn new-player []
  (atom
   {:id (get-new-id)
    :name ""
    :balance nil
    :loot nil}))

(defonce app-state
  (atom {:overall-balance 0
         :average-balance 0
         :players {}}))
;;FIXME instead of calculating balances, store them in the state, then display diff of actual and average balance per player

(defn parse-num
  "Parses a string into a number.
  If the string ends with k, multiplies the number by 1000.
  If the string ends with kk or m, multiplies the number by 1,000,000.
  The suffix detection is case insensitive."
  [s]
  (if (nil? s)
    0
    (let [s (.toLowerCase s)
          num (js/parseFloat s)]
      (* num
         (cond
           (.endsWith s "kk") 1000000
           (.endsWith s "m") 1000000
           (.endsWith s "k") 1000
           :else 1)))))

(defn shorten-num
  [n]
  (cond
    (>= n 1000000) (gstring/format
                    "%.2fm"
                    (/ n 1000000))
    (>= n 1000) (gstring/format
                 "%.2fk"
                 (/ n 1000))
    :else n))

(defn get-players
  []
  (-> @app-state :players vals))


(defn get-balances
  []
  (->> (for [player (get-players)]
         (-> @player :balance parse-num))
       (filter some?)))

(defn calc-average-balance
  []
  (let [player-count (count (get-players))]
    (if (= 0 player-count)
      0
      (->
       (->> (get-balances)
            (reduce + 0))
       (/ player-count)))))

(defn average-balance
  []
  [:div
   "Average balance: "
   (shorten-num (calc-average-balance))])

(defn player-attribute-text-field [player attribute]
  [:input
   {:type "text"
    :value (attribute @player)
    :on-change (fn update-player-attribute
                 [e]
                 (swap! player #(assoc % attribute (-> e .-target .-value))))}])

(defn player-row [player]
  [:tr
   [:td
    (player-attribute-text-field player :name)]
   [:td
    (player-attribute-text-field player :balance)]
   [:td
    (- (calc-average-balance) (-> @player :balance parse-num))]
   #_[:td
      (player-attribute-text-field player :loot)]
   [:td
    [:input
     {:type "button"
      :value "Remove Player"
      :on-click (fn remove-player
                  [_]
                  (swap! app-state dissoc (:id @player)))}]]])

(defn add-player-button
  []
  [:input
   {:type "button"
    :value "Add Player"
    :on-click (fn add-player
                [_]
                (let [player (new-player)]
                  (swap! app-state assoc-in [:players (:id @player)] player)))}])

(defn overall-balance
  []
  [:div
   "Overall balance: "
   (->> (get-balances)
        (reduce + 0)
        shorten-num)])


(defn hunt-calc-page []
  [:div
   [:h1 "Team Hunt Balance Calculator"]
   [:table
    [:thead
     [:tr
      [:th "Player"]
      [:th "Balance"]
      [:th "Difference"]]]
    [:tbody
     (doall
      (for [player (get-players)]
        ^{:key (:id @player)}
        [player-row player]))]]
   [add-player-button]
   [:h3 "Results:"]
   [overall-balance]
   [average-balance]])

(reagent/render-component [hunt-calc-page]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (println app-state))
