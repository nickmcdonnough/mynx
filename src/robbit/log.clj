(ns robbit.log)

(def ^:private map' (comp dorun map))

(defonce log-str (atom ""))

(defn log [& s] (->> (swap! log-str #(str (apply str s)
  "\n----------------------------------------------------\n"
                                          %))
                     (spit "log.txt")))

(defn load-log [] (reset! log-str (slurp "log.txt")))

(defn reset-log [] (reset! log-str ""))

(defn print-log []
  (println @log-str))
