(ns default.d
  (:require [quil.core :as q])
  (:require [incanter.interpolation :as interpol]))

(defn setup []
  (q/background 255))

(defn generate-layers [min-x max-x min-y max-y layer-n]
  (let [yst (/ (- max-y min-y) (dec layer-n))]
    (for [y (range layer-n)]
      (map (fn [x] [x (+ min-y (* y yst))]) (sort (repeatedly (+ 2 (rand-int 6)) #(q/lerp min-x max-x (rand))))))))

(defn gen-shapes [layers y-margin]
  (for [i (-> layers count dec range)]
    (let [layer-1 (map (fn [[x y]] [x (+ y (/ y-margin 2))]) (nth layers i))
          layer-2 (map (fn [[x y]] [x (- y (/ y-margin 2))]) (reverse (nth layers (inc i))))
          shape (concat layer-1 layer-2)]
      shape)))

(defn draw []
  (q/no-loop)
  (let [tower-count 8
        x-min (* 0.05 (q/width)) x-max (* 0.95 (q/width)) x-diff (- x-max x-min) tower-width (/ x-diff tower-count)
        towers-layers (for [i (range tower-count)]
                        (let [min-x (+ x-min (* tower-width i))
                              max-x (+ x-min (* tower-width (inc i)))
                              min-y (* 0.05 (q/height))
                              max-y (* 0.95 (q/height))]
                          (generate-layers min-x max-x min-y max-y 14)))

        towers-shapes (for [layers towers-layers]
                        (gen-shapes layers (/ (q/height) 80)))
        line-count 15
        towers-shapes-fns (map-indexed (fn [idx val] [idx val])
                                       (for [shapes towers-shapes]
                                         (for [points shapes]
                                           (repeatedly line-count #(let [pt-n 3
                                                                         local-points (take pt-n (shuffle points))]
                                                                     [(interpol/interpolate (map-indexed (fn [idx [x _]] [(/ idx (dec pt-n)) x]) local-points) :cubic-hermite)
                                                                      (interpol/interpolate (map-indexed (fn [idx [_ y]] [(/ idx (dec pt-n)) y]) local-points) :cubic-hermite)])))))
        clr-1 (q/color 221, 62, 84) clr-2 (q/color 107, 229, 133)
        clr-1 (q/color 48, 232, 191) clr-2 (q/color 255, 130, 53)
        clr-1 (q/color 239, 239, 187) clr-2 (q/color 212, 211, 221)
        clr-1 (q/color 213, 51, 105) clr-2 (q/color 203, 173, 109)
        clr-1 (q/color 253, 252, 71) clr-2 (q/color 36, 254, 65)
        clr-1 (q/color 178, 254, 250) clr-2 (q/color 14, 210, 247)]
    (q/background 0)
    (q/stroke-weight (/ (q/width) 800))
    (q/rect-mode :center)
    (q/stroke 255)
    (q/no-fill)
    (doseq [[idx shapes-fns] towers-shapes-fns]
      (q/stroke (q/lerp-color clr-1 clr-2 (q/map-range idx 0 (-> towers-shapes-fns count dec) 0 1)))
      (doseq [shape-fns shapes-fns]
        (doseq [[fn-x fn-y] shape-fns]
          (q/begin-shape)
          (doseq [t (range 0 1 0.05)]
            (q/vertex (fn-x t) (fn-y t)))
          (q/end-shape)))))
  (q/save "gen/Reflections 1.png"))