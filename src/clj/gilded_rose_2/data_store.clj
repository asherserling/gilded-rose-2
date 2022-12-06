#_{:clj-kondo/ignore [:namespace-name-mismatch]}
(ns gilded-rose-2.data-store
  (:require [gilded-rose-2.inventory :refer [update-inventory initial-inventory]]))

(def data-store (atom {:inventory initial-inventory}))

(comment 
   
)