(ns scrape.parse
  (:require [httpurr.client.aleph :as http]
            [net.cgrand.enlive-html :as html]
            [scrape.utils :refer [prep]]))

(defn extract-str [v]
  (if (string? v) v (first v)))

(defn parse-item-duration [dur-str]
  (if-let [[_ h m s] (re-find dur-str #"(\d+)?(?::)?(\d{2}):(\d{2})")]
    (+ (read-string s)
       (* 60 (read-string m))
       (-> h (or "0") read-string (* 360)))
    :podcasts.duration/ERROR))

(defn parse-feed-duration [items]
  (let [durations (into [] (comp (map :duration) (filter number?)) items)]
    (if (empty? durations) :podcasts.duration/ERROR
                           (/ (reduce + 0 durations)
                              (count durations)))))

(defn parse-explicit [explicit-str]
  (-> explicit-str #{"yes" "true" "explicit" "dirty"} some?))

(defn- parse-explicit [& _] :podcasts.explicit/not-set)
(defn- parse-keywords [& _] :podcasts.keywords/not-set)
(defn- parse-schedule [& _] :podcasts.schedule/not-set)

(defrecord Item [title
                 pub-date
                 creator
                 description
                 explicit
                 duration])

(defrecord Feed [title
                 author
                 feed-url
                 summary
                 explicit
                 keywords
                 category
                 schedule
                 duration
                 items])

(defn content-from [node selectors]
  (-> node (html/select (conj selectors html/content)) extract-str))

(defn parse-item [item-node]
  (when-let [duration (-> item-node (content-from [:itunes:duration]) parse-item-duration)]
    (let [title (content-from item-node [:title])
          pub-date (content-from item-node [:pubDate])
          creator (content-from item-node [:dc:creator])
          description (content-from item-node [:description])
          explicit (-> item-node (content-from [:itunes:explicit]) parse-explicit)]
      (->Item title pub-date creator description explicit duration))))

(defn parse-feed [category feed-snippet]
  (let [title (content-from feed-snippet [:channel :> :title])
        author (content-from feed-snippet [#{:itunes:author [:itunes:owner :itunes:name]}])
        url (content-from feed-snippet [:atom:link])
        summary (content-from feed-snippet [:itunes:summary])
        explicit (-> feed-snippet (content-from [:itunes:explicit]) parse-explicit)
        items (mapv parse-item (html/select feed-snippet [:item]))
        keywords (parse-keywords feed-snippet items)
        duration (parse-feed-duration items)
        schedule (parse-schedule items)
        ]
    (->Feed title author url summary explicit keywords category schedule duration items)
    ))

(defn get-feed [{:keys [category feed-url]}]
  (parse-feed category (-> feed-url http/get prep)))
