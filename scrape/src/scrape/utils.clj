(ns scrape.utils
  (:require [net.cgrand.enlive-html :as html])
  )

(defn prep [http-promise] (-> http-promise deref :body slurp html/html-snippet))

