# Mynx

    [mynx "2.0.0"]

Mynx is an easy, yet powerful, way to interact with [reddit](http://www.reddit.com/) with Clojure. Please see the [wiki](https://github.com/one-more-minute/mynx/wiki) for documentation.

Mynx powers [Marvin](http://github.com/one-more-minute/The-Paranoid-Android), a bot which has served /r/scp reliably for the past two years or so now.

## Introduction

You can enter the following snippets straight into a repl.

```clj
(use 'reddit)
```

Mynx is probably a little different from other reddit wrappers - but hopefully you'll like it.

Although it's not necessary for just retreiving information, you may as well log in first - there's more than one way to do this but the easiest is to do it globally:

```clj
(login! "username" "password")
```

Ok, first things first: Mynx embraces URLs rather than hiding them, but they're easy to construct:

```clj
(user-comments "one_more_minute")
;=> "http://www.reddit.com/user/one_more_minute/comments/"

(subreddit-comments '[funny aww])
;=> "http://www.reddit.com/r/funny+aww/comments/"
```

Next we'll use the `items` function, which turns this URL into a lazy sequence of all items at that location. As this could be thousands of things, we'll just have a look at one - the current top link on r/funny:

```clj
(->> "funny" subreddit items first)
;=> {:over_18 false, :banned_by nil, :is_self false, ...}
```

Links and comments are just data, and almost exactly the same as reddit's JSON output. Simple. Let's show off how much karma I have:

```clj
(->> "one_more_minute" user-comments items (map :score) (reduce +))
;=> 746
```

Alright, let's try something more interesting.

```clj
(->> "funny" subreddit-comments new-items first)
;=> {:banned_by nil, :edited false, :kind :comment, ...}
```

This might look the same as before, but it's not. Where `items` constructs a list of things already on the page, `new-items` constructs a list of items which *will* be on the page - an infinite sequence of future links/comments/whatever. Sceptical? Try this:

```clj
(->> "all" subreddit-new items (map :title) (map println) dorun)
```

This will print the titles of new links posted to reddit, as they appear, indefinitely (although note that you should be logged in for this to work well). You may also note that they appear in batches every two seconds - Mynx respects reddits API rules for you by default.

The really cool thing about this is that when you write a bot, for example, you don't have to think about loops or timing or any other kind of plumbing or boilerplate - you just map a function over all future comments/links, and it works. And if you do need something more complex, it's easy to drop back down to `items-after`, `items` etc. all the way to `get-parsed` for more control.

Let's write a bot which replies to all comments in /r/sandbox containing the text "hello, bot":

```clj
(login! "username" "password")
(set-user-agent! "Mynx Demo")

(->> "sandbox" subreddit-comments new-items
     (filter #(re-find #"hello, bot" (:body %)))
     (map #(reply % "    hello, human"))
     dorun)
```

Neat, huh?
