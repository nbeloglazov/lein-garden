# garden

A Leiningen plugin for [garden] lib that automatically compiles garden files to css.

## Install

Add lein-garden to plugins section in `project.clj`:

```clojure
:plugins [[lein-garden "0.1.0-SNAPSHOT"]]
```

Add garden config to `project.clj`:

```clojure
:garden {:source-path "src/garden"
           :output-path "resources/css"}
```

### Usage

lein-garden provides 2 tasks: 

* `lein garden once` - compiles garden files to css once
* `lein garden auto` - compiles garden files to css on every change

## File format

Garden files are usual clojure files (*.clj). Plugin reads all expressions from clj files, evaluates them, retains vectors and garden.types.CSSAtRule values and compiles them to css.

## Example:

simple.clj
```clojure
[:body {:font-size "16px"}]

[:h1 :h2 {:font-weight "none"}]

[:h1 [:a {:text-decoration "none"}]]

[:h1 :h2 [:a {:text-decoration "none"}]]

[:h1 :h2 {:font-weight "normal"}
  [:strong :b {:font-weight "bold"}]]

[:a
  {:font-weight 'normal
   :text-decoration 'none}
  [:&:hover
    {:font-weight 'bold
     :text-decoration 'underline}]]
```

complex.clj
```clojure
(require '[garden.color :as color :refer [rgb]]
         '[garden.units :refer [px]]
         '[garden.stylesheet :refer [at-media]])

(def font-color-active (rgb 0 0 0))

(def font-color-inactive (color/lighten font-color-active 50))

[:body :p
  {:color font-color-active}

  [:.inactive
    {:color font-color-inactive}]]

(at-media {:min-width (px 768) :max-width (px 979)}
          [:container {:width (px 960)}])
```

Check [examples](https://github.com/nbeloglazov/lein-garden/tree/master/examples) project.

## License


Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
