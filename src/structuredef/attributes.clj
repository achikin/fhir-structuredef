(ns structuredef.attributes
  (:require [structuredef.helpers :as helpers]
            [structuredef.valueset :as valueset]))

(def generated-fields [:id :valueset :attrs :isRequired :isArray])


(defmulti parse-attr-field first)

(defmethod parse-attr-field :default [data]
  data)

(defmethod parse-attr-field :desc [data]
  [:description (second data)])

(defmethod parse-attr-field :type [data]
  [:type [{:code (second data)}]])


(defn get-cardinality [attr]
  (let [required? (:isRequired attr)
        is-array? (:isArray attr)]
    {:min (if required? "1" "0")
     :max (if is-array? "*" "1")}))


(declare parse-attrs)

(defn parse-attr [base-url parent attr]
  (let [attr-name (first attr)
        id (format "%s.%s" parent (name attr-name))
        data (second attr)
        fields (->> (apply dissoc data generated-fields)
                 (map parse-attr-field)
                 helpers/fields->map)
        cardinality (get-cardinality data)
        value-set (when (:valueset data)
                    (valueset/process-valueset base-url (:valueset data)))
        parsed-attrs (->> data
                       :attrs
                       (parse-attrs base-url id))]
    {:attrs
     (conj (:attrs parsed-attrs)
       (merge {:id id
               :path id}
         (:nodes value-set)
         cardinality
         fields ))
     ;;here goes any defs from extensions and valueset
     :defs (conj (:defs parsed-attrs) (:def value-set))}))

(defn parse-attrs [base-url parent attrs]
  (when attrs
    (let [result (map (partial parse-attr base-url parent) attrs)
          attrs (remove nil? (flatten (map :attrs result)))
          defs (remove nil? (flatten (map :defs result)))]
      {:attrs attrs
       :defs defs})))
