(ns bestof)

(defn p [ups downs]
  (/ ups
     (+ ups downs)))

(defn wilson [ups downs]
  (let [p (p ups downs)
        z 1.96]))