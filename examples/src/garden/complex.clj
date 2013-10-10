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
