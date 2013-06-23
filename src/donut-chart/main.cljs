(ns main
  (:require [strokes :refer  [d3]]
            [clojure.string :as s]
            [data :as data])
  )

(strokes/bootstrap)

(def data (let [[header & body]
                (for [line (filter identity (s/split-lines data/data))]
                  (s/split line ","))]
            (for [[age population] body] (into {} (map (fn [k v] [(keyword k) v]) header [age (js/parseFloat population)])))))


(def width 960)
(def height 500)
(def radius  (/ (Math/min width height) 2))

(def color (-> d3
               .-scale
               (.ordinal)
               (.range ["#98abc5" "#8a89a6" "#7b6888" "#6b486b" "#a05d56" "#d0743c" "#ff8c00"])
               ))
(def arc (-> d3
             .-svg
             (.arc)
             (.outerRadius (- radius 10))
             (.innerRadius (- radius 70))
             ))

(def pie (-> d3
             .-layout
             (.pie)
             (.sort nil)
             (.value #(:population %))))

(def svg (-> d3
             (.select "body")
             (.append "svg")
             (.attr "width" width)
             (.attr "height" height)
             (.append "g")
             (.attr "transform" (format "translate(%d, %d)" (/ width 2) (/ height 2)))
             ))
(def g (-> svg
           (.selectAll ".arc")
           (.data (pie data))
           (.enter)
           (.append "g")
           (.attr "class" "arc")
           ))
(-> g
    (.append "path")
    (.attr "d" arc)
    (.style "fill" (fn [d] (color (:age (.-data d)))))
    )

(-> g
    (.append "text")
    (.attr "transform" #(format "translate(%s)" (.centroid arc  %)))
    (.attr "dy" ".35em")
    (.attr "text-anchor" "middle")
    (.text (fn [d] (:age (.-data d))))
    )

