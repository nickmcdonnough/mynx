(ns reddit.url
  (use clarity.core)
  (require [clojure.string :as str]))

(use-clarity)
(clarity

defmacro reddit
  "Macro, turns `(reddit api eg)` into 'http://www,reddit.com/api/eg'"
  [& rest]
  str "http://www.reddit.com/" : str/join "/" rest

defn subreddit
  """Gives the url for the given subreddit,
  in the form "sub", :sub, 'sub or a list."""
  [x]
  (cond
    (some #(% x) [symbol? keyword? string?])
      (str (reddit r) "/" (name x))
    :else
      (subreddit (str/join "+" (map name x))))

defn subreddit-comments
  "Comments page url for a given subreddit(s)."
  [names]
  str (subreddit names) "/comments"

defn subreddit-new
  "New links page url for a given subreddit(s)."
  [names]
  str (subreddit names) "/new"

defn user
  "The user's submissions."
  [username]
  str (reddit user) "/" username

defn about-user
  [username]
  str (reddit user) "/" username "/about"

)