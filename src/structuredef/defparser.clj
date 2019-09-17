(ns structuredef.defparser
  (:require [structuredef.helpers :as helpers]
            [structuredef.attributes :as attributes]))

(def structure-def-meta
  {:resourceType "StructureDefinition"})

(defmulti parse-def-field
  "parses single field in definition exept for :attrs"
  first)

(defmethod parse-def-field :default [data]
  data)

(defmethod parse-def-field :type [data]
  [:type [{:code (second data)}]])

(defmethod parse-def-field :desc [data]
  [:definition (second data)])

(defn parse-definitions [base-url data]
  (let [fields (->> (dissoc data :attrs)
                 (map parse-def-field)
                 helpers/fields->map)
        resource-type (:resourceType data)
        parsed-attrs (attributes/parse-attrs base-url resource-type (:attrs data))
        attrs (:attrs parsed-attrs)
        defs (:defs parsed-attrs)]
    (conj defs
      (merge fields
        structure-def-meta
        (when attrs
          {:differential
           {:element attrs}})))))
