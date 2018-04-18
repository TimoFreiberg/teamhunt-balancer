(ns teamhunt-balancer.components.ui
  (:require [com.stuartsierra.component :as component]
            [teamhunt-balancer.core :refer [render]]))

(defrecord UIComponent []
  component/Lifecycle
  (start [component]
    (render)
    component)
  (stop [component]
    component))

(defn new-ui-component []
  (map->UIComponent {}))
