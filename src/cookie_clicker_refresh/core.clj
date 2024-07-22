(ns cookie-clicker-refresh.core
  (:require [opencv4.core :as cv]
            [opencv4.utils :as utils])
  (:import [java.awt Robot]
           [java.awt.event KeyEvent]))

(defn convert-to-grayscale [image]
  (let [gray (cv/new-mat)]
    (cv/cvt-color image gray cv/COLOR_BGR2GRAY)
    gray))

(defn img
  "Convenience function to ensure that image paths get converted to OpenCV images."
  [v]
  (if (string? v)
    (cv/imread v)
    v))

(defn match-template [source template]
  (let [source (img source)
        template (img template)
        result (cv/new-mat)]
    (cv/match-template source template result cv/TM_CCOEFF_NORMED)
    result))

(defn not-close [points {:keys [x y]}]
  (not
   (or
    (get points {:x (dec x) :y (- y 2)}) ;; TODO there's probably a better way to do this
    (get points {:x (dec x) :y (dec y)})
    (get points {:x (dec x) :y y})
    (get points {:x (dec x) :y (inc y)})
    (get points {:x (dec x) :y (+ y 2)})
    (get points {:x x :y (- y 2)})
    (get points {:x x :y (dec y)})
    (get points {:x x :y y})
    (get points {:x x :y (dec y)})
    (get points {:x x :y (+ y 2)})
    (get points {:x (inc x) :y (- y 2)})
    (get points {:x (inc x) :y (dec y)})
    (get points {:x (inc x) :y y})
    (get points {:x (inc x) :y (inc y)})
    (get points {:x (inc x) :y (+ y 2)}))))

(defn count-template
  ;; E.g. (count-template "several_wheats.png" "wheat.png" 0.7)
  [source template threshold]
  (let [source (img source)
        template (img template)
        result (cv/new-mat)
        locations-atom (atom {})]
    (cv/match-template source template result cv/TM_CCOEFF_NORMED)
    (doseq [y (range (.rows result))
            x (range (.cols result))
            :let [score (first (.get result y x))]
            :when (>= score threshold)
            ]
      (swap! locations-atom (fn [locations]
                              (if (not-close locations {:x x :y y})
                                (assoc locations {:x x :y y} true)
                                locations)))
      ;; (cv/rectangle source
      ;;               (cv/new-point x y)
      ;;               (cv/new-point (+ x (.width template)) (+ y (.height template)))
      ;;               (cv/new-scalar 0 0 255)
      ;;               1)
      )
    ;; (cv/imwrite source "found.png")
    (count @locations-atom)))

(defn find-best-match [result]
  (let [min-max-loc (cv/min-max-loc result)]
    {:max-val (.-maxVal min-max-loc)
     :max-loc (.-maxLoc min-max-loc)}))

(defn template-inside? [source template threshold]
  (let [source (if (string? source)
                 (cv/imread source)
                 source)
        template (if (string? template)
                   (cv/imread template)
                   template)
        source-gray (convert-to-grayscale source)
        template-gray (convert-to-grayscale template)
        result (match-template source-gray template-gray)
        match (find-best-match result)]
    (println "Match value:" (:max-val match))
    (>= (:max-val match) threshold)))

(defn capture-screen []
  (let [filename "./screencap.png"]
    ;; Capture the screen
    (.. (Runtime/getRuntime) (exec (str "rm " filename)) waitFor)
    (.. (Runtime/getRuntime) (exec (str "scrot " filename)) waitFor)
    (println "Just took a screenshot")
    ;; Read the captured image
    (cv/imread filename)))

(defn press-f5 []
  (let [robot (Robot.)]
    ;; Press and release the F5 key
    (.keyPress robot KeyEvent/VK_F5)
    (.keyRelease robot KeyEvent/VK_F5)
    (println "F5 key pressed and released")))

(defn is-wheat? []
  (println "plot-with-wheat.png: ")
  (template-inside? "plot-with-wheat.png" "wheat.png" 0.8)
  (println "plot-without-wheat.png: ")
  (template-inside? "plot-without-wheat.png" "wheat.png" 0.8))

(defn delay-grab []
  (Thread/sleep 2000)
  (template-inside? (capture-screen) "wheat.png" 0.7))

(defn refresh-till [plant-path]
  (Thread/sleep 2000)
  (while (not (template-inside? (capture-screen) plant-path 0.7))
    (press-f5)
    (Thread/sleep 2000)))

;; (refresh-till "Elderwort_bud.png")
