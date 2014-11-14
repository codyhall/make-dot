(ns make-dot.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            ))



;; Need a good way to read in the data
;; should look something like
;;
;;(with-open
;;   (S reset cache
;;   (I add to input index
;;   (O (map print "$I -> $O;" )
;;   (S reset cache
;;   (I add to input index
;;   (O (map print "$I -> $O;" )
;;   (S reset cache
;;   (I add to input index
;;   (O (map print "$I -> $O;" )
;;   (S reset cache
;;   (I add to input index
;;   (O (map print "$I -> $O;" )
;; ... and so on


;; Define the data set, handles parsing \tab into two string elements of the vec

(def file-name "./resources/drake-graph.txt")

(defn read-file [fn]
  (with-open [in-file (io/reader fn)]
    (into []
          (csv/read-csv in-file :separator \tab))))

(defn group-edges [v]
  (partition-by (fn [[c _]] (= c "S")) v))

(defn print-groups [v]
  (let [of (last (last v))]
      (map (fn [[c if]] [if of]) (butlast v))))

(defn trim [f]
  (last (str/split f #"/")))

(defn print-edges [[if of]]
  (str (trim if) " -> " (trim of) ";"))

(defn make-stringy-edges [file-name]
  (->> file-name
       read-file
       drop-last
       group-edges
       (remove (fn [[[c _]]] (= c "S")))
       (map print-groups)
       (mapcat identity)
       (map print-edges)))

(defn to-dot-format [s]
  (apply str
         (concat ["digraph G {"]
                 s
                 ["}"])))

(->> file-name
    make-stringy-edges
    to-dot-format
    (spit "dotf.dot"))
