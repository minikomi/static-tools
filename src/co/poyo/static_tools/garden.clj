(ns co.poyo.static-tools.garden
  (:require [garden.core :as garden]
            [mount.core :as mount]
            [me.raynes.fs :as fs]
            [co.poyo.static-tools.config :as config]
            [co.poyo.static-tools.target-dir :refer [target-dir]]
            [co.poyo.watch-and-run :as watch-and-run]
            [co.poyo.watch-and-run.file-map :as file-map]
            [clojure.java.io :as io]))

(def DEFAULT_FILE_MAP "file-maps/css.edn")
(def DEFAULT_TARGET_SUBDIR "css")

(defn get-css-jobs []
  (file-map/load-file-map
   (io/resource (get-in config/env [:file-maps :css] DEFAULT_FILE_MAP))
   {:base-path
    (io/file target-dir
             (get-in config/env [:folders :css] DEFAULT_TARGET_SUBDIR))}))

(mount/defstate
  garden
  "starts building garden templates to css files as defined in the :css file map"
  :start
  (do
    (fs/mkdir
     (io/file target-dir
              (get-in config/env [:folders :css] DEFAULT_TARGET_SUBDIR)))
    (watch-and-run/add-jobs (get-css-jobs)))
  :stop
  (watch-and-run/remove-jobs garden))
