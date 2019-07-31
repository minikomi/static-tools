(ns co.poyo.static-tools.figwheel
  (:require
   [mount.core :as mount]
   [co.poyo.static-tools.config :as config]
   [figwheel.main.api :as figwheel]))

(mount/defstate
  figwheel
  "starts figwheel for each build listed in :cljs-builds"
  :start
  (doseq [build (get config/env :cljs-builds [:dev])]
    (figwheel/start {:mode :serve :open-url false} build))
  :stop
  (figwheel/stop-all))
