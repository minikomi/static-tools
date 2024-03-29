(ns co.poyo.static-tools.handler
  (:require [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [mount.core :refer [defstate]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.default-charset :refer [wrap-default-charset]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.http-response :as http]
            [ring.util.response :as res]
            [co.poyo.static-tools.config :as config]))

;; index handler

(defn wrap-index-for-folders [handler]
  (fn [req]
    (let [new-req (update-in req [:uri]
                             #(if (or (io/resource (str "public" % "/index.html"))
                                      (fs/exists? (fs/file (str (config/env :target) %) "index.html")))
                                (str % "/index.html")
                                %))
          response (handler new-req)]
      (if (and (= 200 (:status response))
               (= java.io.File (type (:body response)))
               (re-find #"index.html$" (.getName (:body response))))
        (assoc-in response [:headers "Content-Type"] "text/html")
        response))))

;; trailing slash

(defn wrap-remove-slash [handler]
  (fn [{:keys [uri] :as req}]
    (if (and (not= uri "/")
             (.endsWith uri "/"))
      (res/redirect (subs uri 0 (- (count uri) 1)))
      (handler req))))

;; static

(defn maybe-wrap-static [handler]
  (if (config/env :static)
    (wrap-file handler (fs/file (config/env :static)))
    handler))

(defn base-handler [req]
  (-> (http/ok (str
                "<h2>404 Not Found.</h2>"
                "<pre>" (with-out-str (clojure.pprint/pprint req)) "</pre>"))
      (res/content-type "text/html")
      (res/status 404)))

(defn wrap-handler []
  (-> base-handler
      (wrap-resource (get-in config/env [:folders :public] "public"))
      (wrap-file (fs/file (get-in config/env [:folders :target] "target")))
      (maybe-wrap-static)
      (wrap-restful-format)
      (wrap-content-type)
      (wrap-index-for-folders)
      (wrap-default-charset "utf-8")))

(defstate wrapped-handler
  :start (wrap-handler))
