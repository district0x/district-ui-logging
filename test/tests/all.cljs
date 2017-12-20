(ns tests.all
  (:require
    [cljs.test :refer [deftest is testing run-tests use-fixtures]]
    [district.ui.logging]
    [mount.core :as mount]
    [taoensso.timbre :as timbre :refer-macros [info warn error]]))

(use-fixtures
  :each
  {:after
   (fn []
     (mount/stop))})

(deftest district-ui-logging-tests
  (-> (mount/with-args
        {:logging {:level :warn}})
    (mount/start))

  (let [{:keys [:level :appenders]} timbre/*config*]
    (is (= :warn level))
    (is (true? (:enabled? (:console appenders))))))
