(ns cookie-clicker-refresh.core
  (:require [opencv4.core :as cv]
            [opencv4.utils :as utils])
  (:import [java.awt Robot]
           [java.awt.event KeyEvent]))

(defn convert-to-grayscale [image]
  (let [gray (cv/new-mat)]
    (cv/cvt-color image gray cv/COLOR_BGR2GRAY)
    gray))

(defn match-template [source template]
  (let [result (cv/new-mat)]
    (cv/match-template source template result cv/TM_CCOEFF_NORMED)
    result))

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
