(ns scrape.core
  (:require [clojure.core.async :as async]
            [cheshire.core :refer [generate-string parse-string]]
            [httpurr.client.aleph :as http]
            [net.cgrand.enlive-html :as html]
            [scrape.scrape :as s]
            [scrape.parse :as p]
            [scrape.utils :refer [prep]]))

(defn -main []
  (let [in-ch (async/chan)
        out-ch (async/chan)]
    (s/scrape-feeds in-ch out-ch)
    out-ch))
