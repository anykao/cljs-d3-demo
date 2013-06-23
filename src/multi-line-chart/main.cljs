(ns main
  (:require [strokes :refer  [d3 category10]]
            [clojure.string :as s]
            [data :as data])
  )

(strokes/bootstrap)

(def parseDate (-> d3
                   .-time
                   (.format "%Y%m%d")
                   .-parse
                   ))
(def color (-> d3 .-scale (.category10)))
(def data (let [[header & body]
                (for [line (filter identity (s/split-lines data/data))]
                  (s/split line #"\s{1,}"))]
            (.domain color (vec (rest header)))
            (for [row body] (into {} (map vector header row)))))
(def cities (vec (for [name (.domain color)]
              {:name name
               :value (for [d data] {:date (parseDate ("date" d)) :temperature (js/parseFloat (name d))})
               })))
(.log js/console (prn-str data))
(.log js/console (prn-str cities))

(def margin {:top 20 :right 80 :bottom 30 :left 50})
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

(-> x (.domain (-> d3 (.extent data #(parseDate ("date" %))))))
(-> y (.domain [(.min d3 cities (fn [d] (.min d3 (:value d) #(:temperature %))))
                (.max d3 cities (fn [d] (.max d3 (:value d) #(:temperature %))))]
               ))
(.log js/console (prn-str (.domain x)))
(.log js/console (prn-str (.domain y)))

(def line (-> d3
              .-svg
              (.line)
              (.interpolate "basis")
              (.x #(x (:date %)))
              (.y #(y (:temperature %)))
              ))

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
(def city (-> svg
              (.selectAll ".city")
              (.data cities)
              (.enter)
              (.append "g")
              (.attr "class" "city")
              ))
(-> city
    (.append "path")
    (.attr "class" "line")
    (.attr "d" #(line (:value %)))
    (.style "stroke" #(color (:name %)))
    )

(-> city
    (.append "text")
    (.datum (fn [d] {:name (:name d)
               :value (last (:value d))}))
    (.attr "transform" #(format "translate(%d, %d)" (x (:date (:value %))) (y (:temperature (:value %)))))
    (.attr "x" 3)
    (.attr "dy" ".35em")
    (.text #(:name %))
    )
