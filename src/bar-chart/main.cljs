(ns main
  (:require [strokes :refer  [d3]]
            [clojure.string :as s]
            [data :as data])
  )

(strokes/bootstrap)

(def formatPercent (-> d3 (.format ".0%")))

(def data (let [[header & body]
                (for [line (filter identity (s/split-lines data/data))]
                  (s/split line #"\s{1,}"))]
            (for [[letter frequency] body] (into {} (map (fn [k v] [(keyword k) v]) header [letter (js/parseFloat frequency)])))))

(.log js/console (prn-str data))

(def margin {:top 20 :right 20 :bottom 30 :left 40})
(def width  (- 960 (:left margin) (:right margin)))
(def height  (- 500 (:top margin) (:bottom margin)))

(def x (-> d3
           .-scale
           (.ordinal)
           (.rangeRoundBands [0 width] 0.1)
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
                (.tickFormat formatPercent)
                ))
(def svg (-> d3
             (.select "body")
             (.append "svg")
             (.attr "width" (+ width (:left margin) (:right margin)))
             (.attr "height" (+ height (:top margin) (:bottom margin)))
             (.append "g")
             (.attr "transform" (format "translate(%d, %d)" (:left margin) (:top margin)))
             ))

(-> x (.domain (map :letter data)))
(-> y (.domain [0 (-> d3 (.max data #(:frequency %)))]))

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
    (.text "Frequency")
    )
(-> svg
    (.selectAll ".bar")
    (.data data)
    (.enter)
    (.append "rect")
    (.attr "class" "bar")
    (.attr "x" #(x (:letter %)))
    (.attr "width" (.rangeBand x))
    (.attr "y" #(y (:frequency %)))
    (.attr "height" #(- height (y (:frequency %))))
    )

