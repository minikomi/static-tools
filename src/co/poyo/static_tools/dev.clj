(ns co.poyo.static-tools.dev
  (:gen-class)
  (:require
   [co.poyo.static-tools.opt :as opt]
   ;; always loaded
   [co.poyo.static-tools.config      :as config]
   [co.poyo.static-tools.nrepl       :as n]
   [co.poyo.static-tools.target-dir  :as t]
   [co.poyo.static-tools.http-server :as h]
   ;; switchable
   [co.poyo.static-tools.pages    :as p]
   [co.poyo.static-tools.garden   :as g]
   [co.poyo.static-tools.figwheel :as f]
   ;; util
   [clojure.pprint :as pprint]
   [clojure.java.io :as io]
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
  (let [{:keys [css js pages] :as opts} (opt/parse args)
        not-mounted (cond-> #{}
                      (not css)   (conj #'g/garden)
                      (not js)    (conj #'f/figwheel)
                      (not pages) (conj #'p/simple-pages))]
    (println "OPTS:" opts)
    (timbre/info
     (with-out-str
       (pprint/pprint
        (-> (mount/except not-mounted)
            (mount/start)))))))
