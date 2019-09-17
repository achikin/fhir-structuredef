(ns structuredef.core
  (:gen-class)
  (:require [yaml.core :as yaml]
            [structuredef.defparser :as parser]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def patient
  (yaml/from-file "resources/rucorepatient.yaml"))

(def base-url "http://hl7.org/fhir/ru/core/")

(def parsed (parser/parse-definitions base-url patient))

(clojure.pprint/pprint parsed)
