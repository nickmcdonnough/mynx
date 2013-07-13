`[robbit "1.0.0"]`

# reddit.clj - the reddit api

The `reddit` namespace provides easy access to the reddit api. Highlights include infinite sequences of items from a given page:

    (->> "clojure" subreddit items (map :title) (take 1000))

(Titles of the 1000 most "hot" items from the clojure subreddit)

As well as the magic of macros providing a very convenient and reliable way to play by the api rules (that is, no more than one call every two seconds):

    (time
      (dorun
        (pmap #(api-call (println %)) (range 5))))
    ;=> "Elapsed time: 8003.141613 msecs"

5 calls in 8 seconds - so it's both optimal and thread safe! (i.e. much better than using Thread/sleep)

## reddit objects: links and comments

For the most part, reddit.clj parses reddit's JSON objects directly into Clojure maps (except "listings" become lists) - so see the [reddit API docs](https://github.com/reddit/reddit/wiki/API) for info on what's available. It adds a couple of useful extra keys to comments and links, e.g. `:time`, an #instance representing the submission time. reddit.clj is very repl-friendly, so it's easy explore what's available.

    (use 'reddit 'reddit.url)

    (-> "clojure" subreddit items first keys)
    ;=> (:title :author :is_self :score ...)

    (-> "clojure" subreddit-comments items first keys)
    ;=> (:author :body :score :permalink ...)

    (def l (login "username" "password"))
    (me l)
    ;=> {:kind :account, :comment_karma 147, :has_mail false ...}

## api coverage

I haven't tried to cover every possible API function. Instead, I've tried to provide a nice interface to the useful stuff, as well as a solid foundation for implementing new functionality if necessary. If you do need to use the API in a new way, you should find that using abstractions like `items` alongside the lower-level functions in `reddit.core` (`get-parsed` and `post`) makes implementing it a piece of cake. Just look at `reddit.clj` itself and you'll see that almost anything can be done in four or five lines.

*That said*, if you'd like new features, or have written something yourself that could be included, please do [pm me](http://www.reddit.com/message/compose/?to=one_more_minute) and I'll see what I can do.

## formatting

`reddit.format` has some useful functions for markdown formatting.

# robbit - bots made easy

<img src="http://i.imgur.com/l5K9A.jpg" width="200" align="right" margin="10px" />

Then we have `robbit`, which lets you easily make reddit bots. Here's a simple bot which replies to every new comment.

    (robbit/start
      {:handler    (fn [comment]
                     {:reply (str "Great comment, " (comment :author) "!")
                      :vote  :up})
       :login      (reddit/login "username" "password")})

That's about as simple as it gets, but you'll probably want to make use of some of the other options:

    :user-agent - Short description + main account username.
    :type       - :comment/:link - choose which to respond to. Defaults to comment.
    :handler    - fn to take a comment/link object and return a response map. Default just returns `nil`.
    :subreddits - String or vector of strings. Load comments/links from here. Defaults to "all".
    :login      - Use reddit/login to generate a login.
    :interval   - Minutes between successive runs - default 2.
    :last-run   - The inital run will load items up to this Date. Defaults to now.
    :delay      - Don't respond to items until they are n minutes old - default 0.

The handler returns a map of actions, as demonstrated above - currently only `:reply` and `:vote` are supported, but it's easy to add custom responses to the multimethod - see `robbit.response` for details.

# more info

There are both marginalia and codox docs in /docs. I've tried to keep eveything clear, but if anything isn't obvious let me know.

    Copyright (c) 2012 Mike Innes
    
    MIT License
    
    Permission is hereby granted, free of charge, to any person obtaining
    a copy of this software and associated documentation files (the
    "Software"), to deal in the Software without restriction, including
    without limitation the rights to use, copy, modify, merge, publish,
    distribute, sublicense, and/or sell copies of the Software, and to
    permit persons to whom the Software is furnished to do so, subject to
    the following conditions:
    
    The above copyright notice and this permission notice shall be
    included in all copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
    LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
    OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
    WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
