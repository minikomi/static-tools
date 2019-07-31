(ns co.poyo.static-tools.dev
  (:gen-class)
  (:require
   [co.poyo.static-tools.nrepl :as n]
   [co.poyo.static-tools.pages]
   [co.poyo.static-tools.garden]
   [co.poyo.static-tools.http-server]
   [co.poyo.static-tools.figwheel]
   [co.poyo.static-tools.config :as config]
   [clojure.pprint :as pprint]
   [mount.core :as mount]
   [taoensso.timbre :as timbre]
   [me.raynes.fs :as fs]))

(defn start! []
  (->
   (mount/except [#'n/nrepl])
   (mount/start)))

(defn stop! []
  (->
   (mount/except [#'n/nrepl])
   (mount/stop)))

(defn restart! []
  (stop!)
  (start!))

(defn -main [& args]
  (mount/start (mount/only #{#'config/env}))
  (when (fs/exists? (:target config/env))
    (timbre/infof "Cleaning target dir [%s]"
                 (:target config/env))
    (fs/delete-dir (:target config/env)))
  (fs/mkdir (:target config/env))
  (timbre/info "-={ MOUNTING }=-")
  (timbre/info
   (with-out-str (pprint/pprint
                  (mount/start)))))
