(ns main
  (:require [strokes :refer  [d3]]
            [clojure.string :as s]
            [data :as data])
  )
(strokes/bootstrap)
(def parseDate (-> d3
                   .-time
                   (.format "%b %Y")
                   .-parse
                   ))
(def data (let [[header & body]
                (for [line (filter identity (s/split-lines data/data))]
                  (s/split line ","))]
            (for [[date price] body] (into {} (map (fn [k v] [(keyword k) v]) header [(parseDate date) (js/parseFloat price)])))))

;;(.log js/console (prn-str data))

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
;;; 本来なら、ここさえdeclare すれば、 brushed は一番最後で定義すれば、良いのはずですが、
;;; なんか、うまくいかない
;;; (declare brushed)
;(declare brush focus area)
;(defn brushed []
  ;(.log js/console "here i come")
  ;(.domain x (if (.empty brush) (.domain x2) (.extent brush)))
  ;(-> focus (.select "path") (.attr "d" area))
  ;(-> focus (.select ".x.axis") (.call x-axis))
  ;)
(def brush (-> d3
               .-svg
               (.brush)
               (.x x2)
               ))

(def area (-> d3
              .-svg
              (.area)
              (.interpolate "monotone")
              (.x #(x (:date %)))
              (.y0 height)
              (.y1 #(y (:price %)))
              ))
(def area2 (-> d3
              .-svg
              (.area)
              (.interpolate "monotone")
              (.x #(x2 (:date %)))
              (.y0 height2)
              (.y1 #(y2 (:price %)))
              ))

(def svg (-> d3
             (.select "body")
             (.append "svg")
             (.attr "width" (+ width (:left margin) (:right margin)))
             (.attr "height" (+ height (:top margin) (:bottom margin)))
             ))

(-> svg
    (.append "defs")
    (.append "clipPath")
    (.attr "id" "clip")
    (.append "rect")
    (.attr "width" width)
    (.attr "height" height)
    )

(def focus (-> svg
               (.append "g")
               (.attr "transform" (format "translate(%d, %d)" (:left margin) (:top margin)))))
(def context (-> svg
               (.append "g")
               (.attr "transform" (format "translate(%d, %d)" (:left margin2) (:top margin2)))))

(-> x (.domain (-> d3 (.extent data #(:date %)))))
(-> y (.domain [0 (.max d3 data #(:price %))]))
(.domain x2 (.domain x))
(.domain y2 (.domain y))

(-> focus
    (.append "path")
    (.datum data)
    (.attr "clip-path" "url(#clip)")
    (.attr "d" area)
    )
(-> focus
    (.append "g")
    (.attr "class" "x axis")
    (.attr "transform" (format "translate(%d, %d)" 0 height))
    (.call x-axis)
    )
(-> focus
    (.append "g")
    (.attr "class" "x axis")
    (.call y-axis)
    )

(-> context
    (.append "path")
    (.datum data)
    (.attr "d" area2)
    )
(-> context
    (.append "g")
    (.attr "class" "x axis")
    (.attr "transform" (format "translate(%d, %d)" 0 height2))
    (.call x2-axis)
    )
(-> context
    (.append "g")
    (.attr "class" "x brush")
    (.call brush)
    (.selectAll "rect")
    (.attr "y" -6)
    (.attr "height" (+ height2 7))
    )
(.on brush "brush" (fn []
                     (.domain x (if (.empty brush) (.domain x2) (.extent brush)))
                     (-> focus (.select "path") (.attr "d" area))
                     (-> focus (.select ".x.axis") (.call x-axis))
                     ))
