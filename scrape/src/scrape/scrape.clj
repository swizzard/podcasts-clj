(ns scrape.scrape
  (:require [clojure.core.async :refer [pipeline-blocking chan onto-chan]]
            [net.cgrand.enlive-html :as html]
            [httpurr.client.aleph :as http]
            [scrape.utils :refer [prep]]))

(defn- with-root [url] (str "http://www.blubrry.com" url))

(defn programs-page-links
  "get category links from /programs, put them onto a channel"
  [c]
  (do (onto-chan c (-> "http://www.blubrry.com/programs/" http/get prep (html/select [:div.category-box :a])))
      c))

(defn get-category
  "process a category link"
  [{:keys [href title]}]
  (let [xf (comp
             (map #(get-in % [:attrs :href]))
             (map with-root)
             (map (fn [url] {:category title :url url})))]
    (loop [snippet (-> href with-root http/get prep) links []]
      (let [page-links (into links xf (html/select snippet [:div.item-box :a]))
            next-page (-> snippet (html/select [(html/attr-has :aria-label "Next")]) first :attrs :href)]
        (if (nil? next-page)
          page-links
          (recur (-> next-page with-root http/get prep) page-links))))))

(def more-info (map (fn [page-link] (update page-link :url str "more-info"))))

(defn get-feed
  "get rss feed"
  [{:keys [url category]}]
  (as-> url %
    (http/get %)
    (prep %)
    (html/select % [:a])
    (filter (fn [{:keys [content]}] (= '("RSS Podcast Feed") content)) %)
    (first %)
    (get-in % [:attrs :href])
    {:category category :feed-url %}))

(def scrape-xf
  (comp
    (map :attrs) 
    (mapcat get-category)
    more-info
    (map get-feed)))

(defn scrape-feeds [in-ch out-ch]
  (pipeline-blocking 4 out-ch scrape-xf (programs-page-links in-ch)))

