(ns structuredef.valueset
  (:require [structuredef.helpers :as helpers]))


(def valueset-meta {:resourceType "ValueSet"})

(defn create-valueset [url val]
  (let [fields (->> (dissoc val :concepts :system)
                 (map identity)
                 helpers/fields->map)
        concepts (:concepts val)]
    (merge fields
      valueset-meta
      {:url url}
      {:compose
       {:include {:system (:system val)
                  :concepts concepts} }})))

(defn sanitize-id
  "Sanitize valueset id so it can be used in URL.
  At the moment replaces all non-alphanumeric with -"
  [id]
  (if id
    (clojure.string/replace id #"[^A-Za-z\d]+" "-")
    ;;better throw here. let's do it later
    ""))

(defn process-valueset [base-url val]
  (let [id (sanitize-id (:id val))
        url (str base-url "ValueSet/" id)]
    {:nodes
     {:type [{:code "Coding"}]
      :binding {:strength "required"
                :valueSet url}}
     :def (create-valueset url val)}))
