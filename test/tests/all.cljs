(ns tests.all
  (:require [cljs.test :refer [deftest is testing run-tests use-fixtures]]
            [district.ui.logging.events :as logging]
            [district.ui.logging]
            [mount.core :as mount]
            [re-frame.core :as re-frame]
            [taoensso.timbre :as log]))

(use-fixtures
  :each
  {:after
   (fn []
     (mount/stop))})

(deftest district-ui-logging-tests
  (-> (mount/with-args
        {:logging {:level :info
                   :console? true}})
      (mount/start))
  (let [{:keys [:level :appenders]} log/*config*]
    (is (= :info level))
    (is (true? (:enabled? (:console appenders))))
    (is (nil? (log/error "foo" {:error "bad error" :foo "bar"} ::error)))
    (is (nil? (re-frame/dispatch [::logging/error "foo" {:error "bad error" :foo "bar"} ::error])))
    (is (nil? (re-frame/dispatch [::logging/info "success" {:foo "bar"} ::success])))))
