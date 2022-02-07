(ns default.core
  (:require [quil.core :as q]
            [quil.middleware :as m])
  (:require [default.d :as d]))

(declare default)
(q/defsketch default
  :title "You spin my circle right round"
  :size [4096 4096]

  :setup d/setup
  :draw d/draw
  ;:features [:keep-on-top]
  )

(defn refresh []
  (use :reload 'default.d)
  (.loop default))
