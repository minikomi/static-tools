(ns co.poyo.static-tools.http-server
  (:require
   [co.poyo.static-tools.handler :refer [wrapped-handler]]
   [co.poyo.static-tools.config :refer [env]]
   [mount.core :refer [defstate]]
   [org.httpkit.server :as ohs]
   [taoensso.timbre :as timbre]))

;; mounting

(defn- stop-server! [server]
  (server :timeout 100))

(defstate server
  :start (do (timbre/infof "Server started on port %d" (:port env 3000))
             (ohs/run-server #'wrapped-handler {:port (:port env 3000)}))
  :stop (stop-server! server))
