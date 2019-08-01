(ns co.poyo.static-tools.build
  (:require
   [mount.core :as mount]
   [co.poyo.static-tools.opt :as opt]
   [co.poyo.static-tools.pages :as pages]
   [co.poyo.static-tools.garden :as garden]
   [co.poyo.static-tools.config :as config]
   [co.poyo.static-tools.target-dir  :as t]
   [co.poyo.watch-and-run.file-map :as file-map]
   [taoensso.timbre :as timbre]
   [me.raynes.fs :as fs]
   [figwheel.main.api :as figwheel]
   [clojure.java.io :as io]))

(defn -main [& args]
  (mount/start (mount/only #{#'config/env
                             #'t/target-dir}))
  (let [{:keys [css js pages static] :as opts} (opt/parse args)]
    (println opts)
    (when css
      (doseq [{:keys [build-fn]}
              (garden/get-css-jobs)]
        (build-fn)))
    (when pages
      (doseq [{:keys [build-fn]}
              (pages/get-pages-jobs)]
        (build-fn)))
    (when js
      (doseq [build (get config/env :cljs-builds [:prod])]
        (figwheel/start {:mode :build-once :open-url false} build))
      (when (fs/exists? (io/file (:target config/env) "cljs-out"))
        (fs/delete-dir (io/file (:target config/env) "cljs-out"))))
    (when static
      (doseq [f (fs/list-dir (get config/env :static "./static"))
              :when (not (#{".DS_Store"} (.getName f)))]
        (timbre/infof "copying [%s]\n" (.getName f))
        (if (fs/directory? f)
          (fs/copy-dir f (io/file t/target-dir (.getName f)))
          (fs/copy f (io/file t/target-dir (.getName f))))))))
