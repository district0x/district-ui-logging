(ns district.ui.logging
  (:require
    [devtools.preload]
    [mount.core :as mount :refer [defstate]]
    [taoensso.timbre :as timbre]
    [cljsjs.raven]))

(declare start)
(defstate logging :start (start (:logging (mount/args))))

(def ^:private devtools-level->fn
  {:fatal js/console.error,
   :error js/console.error,
   :warn js/console.warn,
   :info js/console.info,
   :debug js/console.debug,
   :trace js/console.trace})

(def ^:private timbre->sentry-levels
  {:trace  "debug"
   :debug  "debug"
   :info   "info"
   :warn   "warning"
   :error  "error"
   :fatal  "fatal"
   :report "info"})

(defn- decode-vargs [vargs]
  (reduce (fn [m arg]
            (assoc m (cond
                       (qualified-keyword? arg) :log-ns
                       (string? arg) :message
                       (map? arg) :meta)
                   arg))
          {}
          vargs))

(def devtools-appender
  "Simple js/console appender which avoids pr-str and uses cljs-devtools
  to format output"
  {:enabled? true
   :async? false
   :min-level nil
   :rate-limit nil
   :output-fn nil
   :fn
   (fn [data]
     (let [{:keys [level ?ns-str ?line vargs_]} data
           vargs (list* (str ?ns-str ":" ?line) (force vargs_))
           f (devtools-level->fn level js/console.log)]
       (.apply f js/console (to-array vargs))))})

(defn sentry-appender [{:keys [:min-level]}]
  {:enabled? true
   :async? false
   :min-level (or min-level :warn)
   :rate-limit nil
   :output-fn :inherit
   :fn (fn [{:keys [:level :?ns-str :?file :?line :vargs] :as data}]
         (let [{:keys [:message :meta :log-ns]} (decode-vargs vargs)
               {:keys [:error :user :ns :line :file]} meta
               opts (clj->js {:level (timbre->sentry-levels level)
                              :logger (str (or log-ns ns ?ns-str)
                                           "["
                                           (or file ?file) ":" (or line ?line)
                                           "]")
                              :extra meta})]
           (when user
             (-> js/Raven
                 (#(js-invoke % "setUserContext" (clj->js user)))))
           (if error
             (-> js/Raven
                 (#(js-invoke % "captureException" error opts)))
             (-> js/Raven
                 (#(js-invoke % "captureMessage" message opts))))))})

(defn start [{:keys [:level :console? :sentry]}]
  (when sentry
    (-> js/Raven
        (#(js-invoke % "config" (:dsn sentry) (clj->js sentry)))
        (#(js-invoke % "install"))))
  (timbre/merge-config! (merge {:level (keyword level)}
                               {:appenders {:console (when console?
                                                       devtools-appender)
                                            :sentry (when sentry
                                                      (sentry-appender sentry))}})))
