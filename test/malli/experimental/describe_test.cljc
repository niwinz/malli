(ns malli.experimental.describe-test
  (:require [clojure.test :refer [deftest is]]
            [malli.experimental.describe :as med]))

(deftest descriptor-test

  (is (= "a map where {:x -> <integer>}"
         (med/describe [:map [:x int?]])))

  (is (= "a map where {:x (optional) -> <integer>, :y -> <boolean>}"
         (med/describe [:map [:x {:optional true} int?] [:y :boolean]])))

  (is (= "a map where {:x -> <integer>} with no other keys"
         (med/describe [:map {:closed true} [:x int?]])))

  (is (= "a map where {:x (optional) -> <integer>, :y -> <boolean>} with no other keys"
         (med/describe [:map {:closed true} [:x {:optional true} int?] [:y :boolean]])))

  (is (= "a function that takes input: [integer] and returns integer"
         (med/describe [:=> [:cat int?] int?])))

  (is (= "a function that takes input: [integer, one or more of boolean] and returns integer"
         (med/describe [:=> [:cat int? [:+ boolean?]] int?])))

  (is (= "a map where {:j-code -> <keyword, and has length 4>}"
         (med/describe [:map [:j-code [:and
                                       :keyword
                                       [:fn {:description "has length 4"} #(= 4 (count (name %)))]]]])))

  (is (= (med/describe [:map-of {:title "dict"} :int :string])
         "a map (titled: ‘dict’) from <integer> to <string>"))

  (is (= (med/describe [:vector [:sequential [:set :int]]])
         "vector of sequence of set of integer"))

  (is (= "one of <:dog = a map where {:x -> <integer>} | :cat = anything> dispatched by the type of animal"
         (med/describe [:multi {:dispatch :type
                                :dispatch-description "the type of animal"}
                        [:dog [:map [:x :int]]]
                        [:cat :any]])))

  (is (= "one of <:dog = a map where {:x -> <integer>} | :cat = anything> dispatched by :type"
         (med/describe [:multi {:dispatch :type}
                        [:dog [:map [:x :int]]]
                        [:cat :any]])))

  (is (= "Order which is: <Country is a map where {:name -> <an enum of :FI, :PO>, :neighbors (optional) -> <vector of \"Country\">} with no other keys, Burger is a map where {:name -> <string>, :description (optional) -> <string>, :origin -> <a nullable Country>, :price -> <integer greater than 0>}, OrderLine is a map where {:burger -> <Burger>, :amount -> <integer>} with no other keys, Order is a map where {:lines -> <vector of OrderLine>, :delivery -> <a map where {:delivered -> <boolean>, :address -> <a map where {:street -> <string>, :zip -> <integer>, :country -> <Country>}>} with no other keys>} with no other keys>"
         (med/describe [:schema
                        {:registry {"Country" [:map
                                               {:closed true}
                                               [:name [:enum :FI :PO]]
                                               [:neighbors
                                                {:optional true}
                                                [:vector [:ref "Country"]]]],
                                    "Burger" [:map
                                              [:name string?]
                                              [:description {:optional true} string?]
                                              [:origin [:maybe "Country"]]
                                              [:price pos-int?]],
                                    "OrderLine" [:map
                                                 {:closed true}
                                                 [:burger "Burger"]
                                                 [:amount int?]],
                                    "Order" [:map
                                             {:closed true}
                                             [:lines [:vector "OrderLine"]]
                                             [:delivery
                                              [:map
                                               {:closed true}
                                               [:delivered boolean?]
                                               [:address
                                                [:map
                                                 [:street string?]
                                                 [:zip int?]
                                                 [:country "Country"]]]]]]}}
                        "Order"])))

  (is (= "ConsCell <a nullable a vector with exactly 2 items of type: integer, \"ConsCell\">"
         (med/describe [:schema
                        {:registry {"ConsCell" [:maybe [:tuple :int [:ref "ConsCell"]]]}}
                        "ConsCell"]))))
