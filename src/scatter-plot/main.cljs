(ns main
  (:require [strokes :refer  [d3]]
            [clojure.string :as s]
            [data :as data]))

(strokes/bootstrap)

(def data (let [[header & body]
                (for [line (filter identity (s/split-lines data/data))]
                  (s/split line #"\s{1,}"))]
            (for [row body] (zipmap (mapv keyword header)
                                    (conj (mapv js/parseFloat (drop-last row)) (last row))))))

(def color (-> d3 .-scale (.category10)))
(def margin {:top 20 :right 20 :bottom 30 :left 40})
(def width  (- 960 (:left margin) (:right margin)))
(def height  (- 500 (:top margin) (:bottom margin)))

(def x (-> d3
           .-scale
           (.linear)
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
(-> x (.domain (-> d3 (.extent data #(:sepalWidth %)))) (.nice))
(-> y (.domain (-> d3 (.extent data #(:sepalLength %)))) (.nice))

(def svg (-> d3
             (.select "body")
             (.append "svg")
             (.attr "width" (+ width (:left margin) (:right margin)))
             (.attr "height" (+ height (:top margin) (:bottom margin)))
             (.append "g")
             (.attr "transform" (format "translate(%d, %d)" (:left margin) (:top margin)))
             ))

(-> svg
    (.append "g")
    (.attr "class" "x axis")
    (.attr "transform" (format "translate(%d, %d)" 0 height))
    (.call x-axis)
    (.append "text")
    (.attr "class" "label")
    (.attr "x" width)
    (.attr "y" -6)
    (.style "text-anchor" "end")
    (.text "Sepal Width (cm)")
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
    (.text "Sepal Length (cm)")
    )
(-> svg
    (.selectAll ".dot")
    (.data data)
    (.enter)
    (.append "circle")
    (.attr "class" "dot")
    (.attr "r" 3.5)
    (.attr "cx" #(x (:sepalWidth %)))
    (.attr "cy" #(y (:sepalLength %)))
    (.style "fill" #(color(:species %)))
    )
(def legend (-> svg
                (.selectAll ".legend")
                (.data (.domain color))
                (.enter)
                (.append "g")
                (.attr "class" "legend")
                (.attr "transform" #(format "translate(%d, %d)" 0 (* 20 %2)))
                ))

(-> legend
    (.append "rect")
    (.attr "x" (- width 18))
    (.attr "width" 18)
    (.attr "height" 18)
    (.style "fill" color)
    )

(-> legend
    (.append "text")
    (.attr "x" (- width 24))
    (.attr "y" 9)
    (.attr "dy" ".35em")
    (.style "text-anchor" "end")
    (.text identity)
    )

