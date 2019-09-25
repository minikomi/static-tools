(ns co.poyo.static-tools.figwheel
  (:require
   [mount.core :as mount]
   [co.poyo.static-tools.config :as config]
   [figwheel.main.api :as figwheel]))

(mount/defstate
  figwheel
  "starts figwheel for each build listed in :cljs-builds"
  :start
  (apply figwheel/start
         {:mode :serve :open-url false}
         (get config/env :cljs-builds [:dev]))
  :stop
  (figwheel/stop-all))
