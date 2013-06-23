(defproject d3-example "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [prismatic/dommy  "0.1.1"]
                 [net.drib/strokes  "0.5.0"]
                 ]
  :plugins [[lein-cljsbuild "0.3.2"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {
    :builds {
      :margin-convention {
        :source-paths ["src/margin-convention"]
        :compiler {:output-to "resources/public/js/marginconvention.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :area-chart {
        :source-paths ["src/area-chart/"]
        :compiler {:output-to "resources/public/js/areachart.js"
                   :optimizations :simple
                   :pretty-print true}
        :jar true}
      :line-chart {
        :source-paths ["src/line-chart/"]
        :compiler {:output-to "resources/public/js/linechart.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :bivariate-area-chart {
        :source-paths ["src/bivariate-area-chart/"]
        :compiler {:output-to "resources/public/js/bivariateareachart.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :multi-line-chart {
        :source-paths ["src/multi-line-chart/"]
        :compiler {:output-to "resources/public/js/multilinechart.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :bar-chart {
        :source-paths ["src/bar-chart/"]
        :compiler {:output-to "resources/public/js/barchart.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :donut-chart {
        :source-paths ["src/donut-chart/"]
        :compiler {:output-to "resources/public/js/donutchart.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :pie-chart {
        :source-paths ["src/pie-chart/"]
        :compiler {:output-to "resources/public/js/piechart.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :grouped-bar-chart {
        :source-paths ["src/grouped-bar-chart/"]
        :compiler {:output-to "resources/public/js/groupedbarchart.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :stacked-bar-chart {
        :source-paths ["src/stacked-bar-chart/"]
        :compiler {:output-to "resources/public/js/stackedbarchart.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :scatter-plot {
        :source-paths ["src/scatter-plot/"]
        :compiler {:output-to "resources/public/js/scatterplot.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :normalized-stacked-bar-chart {
        :source-paths ["src/normalized-stacked-bar-chart/"]
        :compiler {:output-to "resources/public/js/normalizedstackedbarchart.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
      :focus-context {
        :source-paths ["src/focus-context/"]
        :compiler {:output-to "resources/public/js/focuscontext.js"
                    :optimizations :simple
                    :pretty-print true}
        :jar true}
             }}
  )

