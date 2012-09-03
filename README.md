# robbit

## reddit api

[robbit thread on reddit](http://www.reddit.com/r/Clojure/comments/z0o6u/robbit_reddit_apibots_in_clojure/)

The `reddit` namespace provides easy access to the reddit api. Highlights include infinite sequences of items from a given page:

    (->> (subreddit "clojure") items (map :title) (take 150))

(Titles of the 150 most "hot" items from the clojure subreddit)

Most of the functions are *fairly* self explanatory, but documentation isn't great yet so feel free to ask questions on the reddit thread and I'll respond ASAP. Basically, reddit returns items (comments/links) as json objects, which are simply parsed into clojure maps. These items can then be passed into fns like `reply` and `vote`. These functions also take a `login` object, produced by the `reddit/login` function. Easy-peasy.

## bots made easy

<img src="http://i.imgur.com/l5K9A.jpg" width="200" align="right" />

Then we have `robbit`, which lets you easily make bots which reply to links/comments. Here's a simple bot which replies to every comment on /r/clojure.

    (robbit/start
      ({:handler    (fn [comment]
                      {:reply (str "Great comment, " (comment :author) "!")
                       :vote  :up})
        :subreddits "clojure"
        :login      (reddit/login "username" "password")}))

Yeah, don't run that. But still.

It's also very easy to make your own response types, in addition to `:reply` and `:vote`, using multifns - see `robbit.clj` for details.

This is my first released project, so contributions (pull requests?) are welcome, but only on the condition that you explain how to accept it :). Any questions, requests or suggestions, pm me on reddit (/u/one_more_minute) or see the thread above.