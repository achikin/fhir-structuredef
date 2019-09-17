(ns structuredef.helpers)

(defn fields->map [parse-result]
  (->> parse-result
    flatten
    (apply hash-map)))

(defn is-extension? [attr]
  (contains? (second attr) :url))
