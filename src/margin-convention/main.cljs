(ns main
  (:require [strokes :refer  [d3]]
            [clojure.string :as s])
  )

(strokes/bootstrap)

(def margin {:top 20 :right 20 :bottom 20 :left 20})
(def padding {:top 60 :right 60 :bottom 60 :left 60})
(def outer-width  960)
(def outer-height  500)
(def inner-width  (- outer-width (:left margin) (:right margin)))
(def inner-height  (- outer-height (:top margin) (:bottom margin)))
(def width  (- inner-width (:left padding) (:right padding)))
(def height  (- inner-height (:top padding) (:bottom padding)))

(def x (-> d3
           .-scale
           (.identity)
           (.domain [0 width])
           ))
(def y (-> d3
           .-scale
           (.identity)
           (.domain [0 height])
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
                (.orient "right")
                ))
(def svg (-> d3
             (.select "body")
             (.append "svg")
             (.attr "width" outer-width)
             (.attr "height" outer-height)
             (.append "g")
             (.attr "transform" (format "translate(%d, %d)" (:left margin) (:top margin)))
             ))

(def defs (-> svg
              (.append "defs")))

(-> defs
    (.append "marker")
    (.attr "id" "triangle-start")
    (.attr "viewBox" "0 0 10 10")
    (.attr "refX" 10)
    (.attr "refY" 5)
    (.attr "markerWidth" -6)
    (.attr "markerHeight" 6)
    (.attr "orient" "auto")
    (.append "path")
    (.attr "d" "M 0 0 L 10 5 L 0 10 z")
    )
(-> defs
    (.append "marker")
    (.attr "id" "triangle-end")
    (.attr "viewBox" "0 0 10 10")
    (.attr "refX" 10)
    (.attr "refY" 5)
    (.attr "markerWidth" 6)
    (.attr "markerHeight" 6)
    (.attr "orient" "auto")
    (.append "path")
    (.attr "d" "M 0 0 L 10 5 L 0 10 z")
    )
(-> svg
    (.append "rect")
    (.attr "class" "outer")
    (.attr "width" inner-width)
    (.attr "height" inner-height)
    )
(def g (-> svg
           (.append "g")
           (.attr "transform" (format "translate(%d, %d)" (:left padding) (:top padding)))
           ))
(-> g
    (.append "rect")
    (.attr "class" "inner")
    (.attr "width" width)
    (.attr "height" height)
    )
(-> g
    (.append "g")
    (.attr "class" "x axis")
    (.attr "transform" (format "translate(%d, %d)" 0 height))
    (.call x-axis)
    )
(-> g
    (.append "g")
    (.attr "class" "y axis")
    (.attr "transform" (format "translate(%d, %d)" width 0))
    (.call y-axis)
    )

(-> svg
    (.append "line")
    (.attr "class" "arrow")
    (.attr "x2" (:left padding))
    (.attr "y2" (:top padding))
    (.attr "marker-end" "url(#triangle-end)")
    )

(-> svg
    (.append "line")
    (.attr "class" "arrow")
    (.attr "x1" (/ inner-width 2))
    (.attr "x2" (/ inner-width 2))
    (.attr "y2" (:top padding))
    (.attr "marker-end" "url(#triangle-end)")
    )
(-> svg
    (.append "line")
    (.attr "class" "arrow")
    (.attr "x1" (/ inner-width 2))
    (.attr "x2" (/ inner-width 2))
    (.attr "y1" (- inner-height (:bottom padding)))
    (.attr "y2" inner-height)
    (.attr "marker-start" "url(#triangle-start)")
    )
(-> svg
    (.append "line")
    (.attr "class" "arrow")
    (.attr "x2" (:left padding))
    (.attr "y1" (/ inner-height 2))
    (.attr "y2" (/ inner-height 2))
    (.attr "marker-end" "url(#triangle-end)")
    )
(-> svg
    (.append "line")
    (.attr "class" "arrow")
    (.attr "x1" inner-width)
    (.attr "x2" (- inner-width (:right padding)))
    (.attr "y1" (/ inner-height 2))
    (.attr "y2" (/ inner-height 2))
    (.attr "marker-end" "url(#triangle-end)")
    )
(-> svg
    (.append "text")
    (.text "origin")
    (.attr "y" -8)
    )
(-> svg
    (.append "circle")
    (.attr "class" "origin")
    (.attr "r" 4.5)
    )
(-> g
    (.append "text")
    (.text "translate(margin.left, margin.top)")
    (.attr "y" -8)
    )
