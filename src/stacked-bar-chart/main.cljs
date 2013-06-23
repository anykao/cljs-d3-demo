(ns main
  (:require [strokes :refer  [d3]]
            [clojure.string :as s]
            [data :as data]))

(strokes/bootstrap)

(let [[header & body]
      (for [line (filter identity (s/split-lines data/data))] (s/split line ","))
      ]
  (def ageNames (vec (rest header)))
  (def data
    (reverse (sort-by :total (for [[state & ages] body ]
      (let [data (map (fn [k v] [k (js/parseInt v)]) ageNames ages)]
        (loop [ret []
               y0 0
               x data]
          (if (seq x)
            (let [tuple (first x)
                  ret (conj ret
                            {:name  (first tuple)
                             :y0 y0
                             :y1 (+ y0 (second tuple))}
                            )
                  y0 (+ y0 (second tuple))]
              (recur ret y0 (rest x)))
            {:state state :ages ret :total (:y1 (last ret))})
          )))))
    ))
(.log js/console (prn-str (first data)))

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
           (.rangeRound [height 0])
           ))

(def color (-> d3
               .-scale
               (.ordinal)
               (.range ["#98abc5" "#8a89a6" "#7b6888" "#6b486b" "#a05d56" "#d0743c" "#ff8c00"])
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
                (.tickFormat (.format d3 "0.2s"))
                ))

(def svg (-> d3
             (.select "body")
             (.append "svg")
             (.attr "width" (+ width (:left margin) (:right margin)))
             (.attr "height" (+ height (:top margin) (:bottom margin)))
             (.append "g")
             (.attr "transform" (format "translate(%d, %d)" (:left margin) (:top margin)))
             ))

(-> color (.domain ageNames))
(-> x (.domain (vec (map :state data))))
(-> y (.domain [0 (.max d3 (vec data) #(:total %))]))
(.log js/console (prn-str (.domain y)))

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
    (.text "Population")
    )
(def state (-> svg
               (.selectAll ".state")
               (.data (vec data))
               (.enter)
               (.append "g")
               (.attr "class" "g")
               (.attr "transform" #(format "translate(%d, %d)" (x (:state %)) 0))
               ))

(-> state
    (.selectAll "rect")
    (.data #(:ages %))
    (.enter)
    (.append "rect")
    (.attr "width" (.rangeBand x))
    (.attr "y" #(y (:y1 %)))
    (.attr "height" #(- (y (:y0 %)) (y (:y1 %))))
    (.style "fill" #(color (:name %)))
    )
;;strokes はseqではダメかな
;;必ずVecでWrapする必要がある
(def legend (-> svg
                (.selectAll ".legend")
                (.data (vec (reverse ageNames)))
                (.enter)
                (.append "g")
                (.attr "class" "legend")
                (.attr "transform" #(format "translate(0, %d)" (* 20 %2)))
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

