(ns co.poyo.static-tools.garden
  (:require [garden.core :as garden]
            [mount.core :as mount]
            [co.poyo.static-tools.config :as config]
            [co.poyo.watch-and-run :as watch-and-run]
            [co.poyo.watch-and-run.file-map :as file-map]
            [clojure.java.io :as io]))

(defn get-css-jobs []
  (file-map/load-file-map
   (io/resource (get-in config/env [:file-maps :css] "file-maps/css.edn"))
   {:base-path (io/file (:target config/env) "css")}))

(mount/defstate
  garden
  "starts building garden templates to css files as defined in the :css file map"
  :start
  (watch-and-run/add-jobs (get-css-jobs))
  :stop
  (watch-and-run/remove-jobs garden))
