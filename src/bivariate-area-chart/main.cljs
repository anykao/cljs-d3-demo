(ns main
  (:require [strokes :refer  [d3]]
            [clojure.string :as s]
            [data :as data])
  )

(strokes/bootstrap)


(def parseDate (-> d3
                   .-time
                   (.format "%Y%m%d")
                   .-parse
                   ))
(def data (let [[header & body]
                (for [line (filter identity (s/split-lines data/data))]
                  (s/split line #"\s{1,}"))]
            (for [[date low high] body] (into {} (map (fn [k v] [(keyword k) v]) header [(parseDate date) (js/parseFloat low) (js/parseFloat high)])))))

(def margin {:top 20 :right 20 :bottom 30 :left 50})
(def width  (- 960 (:left margin) (:right margin)))
(def height  (- 500 (:top margin) (:bottom margin)))

(def x (-> d3
           .-time
           (.scale)
           (.range [0 width])
           ))

(def y (-> d3
           .-scale
           (.linear)
           (.range [height 0])
           ))

(def x-axis (-> d3
                .-svg
                (.axis)
                (.scale x)
                (.orient "bottom")
                ))
(def y-axis (-> d3
                .-svg
                (.axis)
                (.scale y)
                (.orient "left")
                ))

(def area (-> d3
              .-svg
              (.area)
              (.x #(x (:date %)))
              (.y0 #(y (:low %)))
              (.y1 #(y (:high %)))
              ))
(def svg (-> d3
             (.select "body")
             (.append "svg")
             (.attr "width" (+ width (:left margin) (:right margin)))
             (.attr "height" (+ height (:top margin) (:bottom margin)))
             (.append "g")
             (.attr "transform" (format "translate(%d, %d)" (:left margin) (:top margin)))
             ))

(-> x (.domain (-> d3 (.extent data #(:date %)))))
(-> y (.domain [(-> d3 (.min data #(:low %))) (-> d3 (.max data #(:high %)))]))

(-> svg
    (.append "path")
    (.datum data)
    (.attr "class" "area")
    (.attr "d" area)
    )
(-> svg
    (.append "g")
    (.attr "class" "x axis")
    (.attr "transform" (format "translate(%d, %d)" 0 height))
    (.call x-axis)
    )
(-> svg
    (.append "g")
    (.attr "class" "y axis")
    (.call y-axis)
    (.append "text")
    (.attr "transform" "rotate(-90)")
    (.attr "y" 6)
    (.attr "dy" ".71em")
    (.style "text-anchor" "end")
    (.text "Temperature (ºF)")
    )

