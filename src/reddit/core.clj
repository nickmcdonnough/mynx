(ns reddit.core
  "1. Functionality for parsing reddit's json
  into usable clojure objects.
  2. Low-level interface to reddit i.e. basic
  retrieval of json / reddit objects from pages,
  and posting of requests. Specific API calls
  are built on top of these."
  (use      reddit.util
            clarity.core)
  (require [clj-http.client :as http]
           [cheshire.core   :as json]))

(use-clarity)
(clarity

;; -------
;; Parsing
;; -------

defn ^:private secs->date [t]
  java.util.Date. : long : * t 1000

defn ^:private trim-id
  "Turns 't3_xvzdh' into 'xvzdh'. * CHANGE THIS"
  [s]
  second : re-find #"_(.+)" s

defn ^:private comment-permalink [{:keys [subreddit link_id id] :as comment}]
  str "http://www.reddit.com/r/" subreddit "/comments/" (trim-id link_id) "/_/" id

;; All reddit objects have a :kind of :link, :comment, or :account, along with relevant data.
;; Comments and links also get :time, with a DateTime object.

defmulti parse
  "Takes reddit's raw objects (as parsed from JSON)
  and turns them into a useful form."
  #(cond
     (vector? %) :vec
     (string? %) :atom
     (nil?    %) :atom
     :else       (:kind %))

defmethod parse :vec [items]
  map parse items

defmethod parse :atom [a] a

;; If a form is not recognised, it is printed and becomes nil.
defmethod parse :default [thing]
  println "Error parsing form:"
  clojure.pprint/pprint thing
  nil

;; Listing objects are converted into lists.
defmethod parse "Listing" [{listing :data}]
  parse : listing :children

;; Comments
defmethod parse "t1" [{{:keys [replies ups downs] :as comment} :data}]
  -> comment
     merge {:kind      :comment
            :permalink (comment-permalink comment)
            :time      (secs->date (comment :created_utc))
            :replies   (parse replies)
            :score     (- ups downs)}

;; Accounts
defmethod parse "t2" [{account :data}]
  -> account
     assoc :kind :account
     dissoc :modhash

;; Links
defmethod parse "t3" [{link :data}]
  -> link
      dissoc :selftext
      merge {:kind      :link
             :permalink (str "http://www.reddit.com" (link :permalink))
             :time      (secs->date (link :created_utc))
             :body      (:selftext link)}

;; "more" objects
defmethod parse "more" [{more :data}]
  -> more
     merge {:kind :more}

;; ------------------
;; Low-level requests
;; ------------------

def ^:dynamic *user-agent*
  "A user agent string used by `request`."
  "reddit.clj"

defn request
  "Request of type :get or :post.
  Options:
      :params     - HTTP parameters
      :login      - reddit login object
      :user-agent - overrides `*user-agent*`"
  [method url & {:keys [params login user-agent]}]
  http/request {:method        method
                :url           url
                :headers       {"User-Agent" (or user-agent *user-agent*)}
                :cookies       (:cookies login)
                :query-params  (merge {:uh (:modhash login)
                                       :rand-int (rand-int 1000000)}
                                      params)}

defn get-json
  "Retrieve and decode json from a web page.
  See `request` for options.
  .json extension added automatically."
  [url & opts]
  (-> (apply request :get (str url ".json") opts)
     :body (json/decode true))

defn get-parsed
  "Same as get-json + parsing."
  [& args]
  parse : apply get-json args

defn post
  "Post with user-agent and login. Returns full clj-http response.
  See `request` for options."
  [url & opts]
  apply request :post url opts

;; -------
;; Caching
;; -------

def ^:private get-json' get-json

def ^:private caching false

defn enable-caching
  "Enable caching of `get-json` requests. Each
  page will be cached for 2 minutes. Useful
  for testing, since multiple requests of
  the same page will cause reddit to 304."
  []
  when-not caching
    def caching true
    def get-json (memoize' get-json (* 2 60 1000))

defn disable-caching
  "Disable caching of requests."
  []
  when caching
    def caching false
    def get-json get-json'

)
