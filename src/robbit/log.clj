(ns robbit.log)

(defonce log-str (atom ""))

(defn file-log
  "Append strings to log.txt"
  [& s]
  (->> #(str %
  	(java.util.Date.) "\n"
             (apply str s)
             "\n----------------------------------------------------\n")
        (swap! log-str)
        (spit "log.txt")))

(defn print-fn
  "Concat and print the strings."
  [& s]
  (println (apply str s)))

(def ^:dynamic *log*
  "`robbit` and `robbit.reponse` use this function
  for logging."
  file-log)

(defn load-log [] (reset! log-str (slurp "log.txt")))

(defn reset-log [] (reset! log-str ""))

(defn print-log []
  (println @log-str))
