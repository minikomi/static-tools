(ns co.poyo.static-tools.opt
  (:require
   [clojure.tools.cli :refer [parse-opts]]))

(def cli-options
  [["-c" "--css"   "build css"]
   ["-j" "--js"    "build js"]
   ["-s" "--static" "copy static to target"]
   ["-p" "--pages" "build html pages"]])

(defn parse [args]
  (if (empty? args) {:css true
                     :js true
                     :pages true}
      (:options (parse-opts args cli-options))))
