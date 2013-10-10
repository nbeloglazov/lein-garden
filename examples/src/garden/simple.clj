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
