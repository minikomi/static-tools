(ns co.poyo.static-tools.pages
  (:require [mount.core :as mount]
            [co.poyo.watch-and-run.file-map :as file-map]
            [co.poyo.watch-and-run :as watch-and-run]
            [co.poyo.static-tools.target-dir :refer [target-dir]]
            [co.poyo.static-tools.config :as config]
            [clojure.java.io :as io]))

(defn get-pages-jobs []
  (file-map/load-file-map
   (io/resource (get-in config/env [:file-maps :pages] "file-maps/pages.edn"))
   {:base-path target-dir}))

(mount/defstate
  simple-pages
  "starts building pages as defined in the :pages file map."
  :start
  (watch-and-run/add-jobs (get-pages-jobs))
  :stop
  (watch-and-run/remove-jobs simple-pages))
