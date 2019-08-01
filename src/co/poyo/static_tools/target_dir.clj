(ns co.poyo.static-tools.target-dir
  (:require
   [me.raynes.fs :as fs]
   [mount.core :refer [defstate]]
   [taoensso.timbre :as timbre]
   [co.poyo.static-tools.config :as config]
   [clojure.java.io :as io]))

(defstate
  target-dir
  :start
  (let [target (io/file (:target config/env))]
    (when (fs/exists? target)
      (timbre/infof "Cleaning target dir [%s]"
                    (:target config/env))
      (fs/delete-dir target))
    (fs/mkdirs target)
    target))
