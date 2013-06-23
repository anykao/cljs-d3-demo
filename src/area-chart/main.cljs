(ns main
  (:require [strokes :refer  [d3]]
            [clojure.string :as s]
            [data :as data])
  )

(strokes/bootstrap)


(def parseDate (-> d3
                   .-time
                   (.format "%d-%b-%y")
                   .-parse
                   ))
(def data (let [[header & body]
                (for [line (filter identity (s/split-lines data/data))]
                  (s/split line #"\s{1,}"))]
            (for [[date close] body] (into {} (map (fn [k v] [(keyword k) v]) header [(parseDate date) (js/parseFloat close)])))))

(def margin {:top 10 :right 10 :bottom 100 :left 40})
(def margin2 {:top 430 :right 10 :bottom 20 :left 40})
(def width  (- 960 (:left margin) (:right margin)))
(def height  (- 500 (:top margin) (:bottom margin)))
(def height2  (- 500 (:top margin2) (:bottom margin2)))

(def x (-> d3
           .-time
           (.scale)
           (.range [0 width])
           ))
(def x2 (-> d3
           .-time
           (.scale)
           (.range [0 width])
           ))

(def y (-> d3
           .-scale
           (.linear)
           (.range [height 0])
           ))
(def y2 (-> d3
           .-scale
           (.linear)
           (.range [height2 0])
           ))

(def x-axis (-> d3
                .-svg
                (.axis)
                (.scale x)
                (.orient "bottom")
                ))
(def x2-axis (-> d3
                .-svg
                (.axis)
                (.scale x2)
                (.orient "bottom")
                ))
(def y-axis (-> d3
                .-svg
                (.axis)
                (.scale y)
                (.orient "left")
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
(-> y (.domain [0 (apply max (map :close data))]))

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
    (.text "Price ($)")
    )

