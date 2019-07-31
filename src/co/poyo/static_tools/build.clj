(ns co.poyo.static-tools.build
  (:require
   [mount.core :as mount]
   [co.poyo.static-tools.pages :as pages]
   [co.poyo.static-tools.garden :as garden]
   [co.poyo.static-tools.config :as config]
   [co.poyo.watch-and-run.file-map :as file-map]
   [me.raynes.fs :as fs]
   [figwheel.main.api :as figwheel]
   [clojure.java.io :as io]))

(defn -main [& args]
  (mount/start (mount/only #{#'config/env}))
  (when (fs/exists? (:target config/env)) (fs/delete-dir (:target config/env)))
  (doseq [job (garden/get-css-jobs)] ((:build-fn job)))
  (doseq [job (pages/get-pages-jobs)] ((:build-fn job)))
  (doseq [build (get config/env :cljs-builds [:prod])]
    (figwheel/start {:mode :build-once :open-url false} build))
  (when (fs/exists? (io/file (:target config/env) "cljs-out"))
    (fs/delete-dir (io/file (:target config/env) "cljs-out"))))
