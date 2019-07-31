(ns co.poyo.static-tools.config
  (:require [mount.core :refer [defstate]]
            [config.core :refer [load-env]]))

(defstate env
  "Loads a yogthos/config environment config"
  :start (load-env))
