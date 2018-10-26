# district-ui-logging

[![Build Status](https://travis-ci.org/district0x/district-ui-logging.svg?branch=master)](https://travis-ci.org/district0x/district-ui-logging)

Clojurescript [re-mount](https://github.com/district0x/d0x-INFRA/blob/master/re-mount.md) module, that takes care of setting up [timbre](https://github.com/ptaoussanis/timbre) + [cljs-devtools](https://github.com/binaryage/cljs-devtools) logging.

## Installation
Add `[district0x/district-ui-logging "1.0.3-SNAPSHOT"]` into your project.clj  
Include `[district.ui.logging]` in your CLJS file, where you use `mount/start`

**Warning:** district0x modules are still in early stages, therefore API can change in a future.

## Usage

- [district.ui.logging](#module)
- [district.ui.logging.events](#events)
  - [::info](#info-event)
  - [:log/info](#info-fx)
  - [::warn](#warn-event)
  - [:log/warn](#warn-fx)
  - [::error](#error-event)
  - [:log/error](#error-fx)

## <a name="module"> district.ui.logging

You can pass following args to logging component: 
* `:level` Min. level that should be logged
* `:console?` Pass true if you want to log into console as well
* `:sentry` [sentry](https://sentry.io/) [configuration options](#sentry)

Log calls take the following arguments:

* `message` (required) string with a human-readable message.
* `meta` (optional) a map with context meta-data.
* `ns` (optional) namespaced keyword for easy aggregating and searching. If none provided the module will try to inferr the namespace. 

Example:

```clojure
(ns my-district
  (:require [mount.core :as mount]
            [district.ui.logging]
            [taoensso.timbre :as log]))

  (-> (mount/with-args
        {:logging {:level :info
                   :console? true}})
    (mount/start))

  (log/error "foo" {:error (js/Error. "bad error")} ::error)
  ;; my-district/error:42 foo {:error "bad error"} :tests.all/error
```

### <a name="sentry"> sentry configuration options
In order to initialize sentry logging appender pass a map of options:

* `:dsn` (required) tells the SDK where to send the events.
* `:min-level` (optional) sets the minimal level of logging to sentry, `:warn` is the default. This setting overrides the timbres `:level` flag!

Example:

```clojure
(-> (mount/with-args
      {:logging {:level :info
                 :console? true
                 :sentry {:dsn "https://4bb89c9cdae14444819ff0ac3bcba253@sentry.io/1306960"
                          :min-level :warn}}})
    (mount/start))
```

## <a name="events"> district.ui.logging.events

```clojure
(ns my-district
  (:require [district.ui.logging.events :as logging-events]))
```

#### <a name="info-event">`::info`
This is an utility event which wraps the [`:log/info`](#info-fx) effect.

```clojure
(ns my-district
  (:require [district.ui.logging.events :as logging]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-fx
 ::my-event
 (fn []
   {:dispatch [::logging/info "success" {:foo "bar"} ::success]}))
```

#### <a name="success-fx">`:log/info`
This is an effect which logs the success to the JS console.

```clojure
(ns my-district
  (:require [district.ui.logging.events]
            [re-frame.core :as re-frame]))
            
(re-frame/reg-event-fx
 ::my-event
 (fn []
   {:log/info ["success" {:foo "bar"} ::success]}))
```

#### <a name="warn-event">`::warn`
This is an utility event which wraps the [`:log/warn`](#warn-fx) effect.

```clojure
(ns my-district
  (:require [district.ui.logging.events :as logging]
            [re-frame.core :as re-frame]))

(re-frame/dispatch [::logging/warn "warning text" {:foo "bar"} ::here])
```

#### <a name="warn-fx">`:log/warn`
This is an effect which logs the success to the JS console.

```clojure
(ns my-district
  (:require [district.ui.logging.events]
            [re-frame.core :as re-frame]))
            
(re-frame/reg-event-fx
 ::my-event
 (fn []
   {:log/warn ["warning text" {:foo "bar"} ::here]}))
```

#### <a name="error-event">`::error`
This is an utility event which wraps the [`:log/error`](#error-fx) effect.

```clojure
(ns my-district
  (:require [district.ui.logging.events :as logging]
            [re-frame.core :as re-frame]))

(re-frame/dispatch [::logging/error "Unexpected Error" {:error (js/Error. "sad error") :foo "bar"} ::my-district])
```
You can also use pass only the JS Error: 
 
```clojure
(re-frame/dispatch [::logging/error (js/Error. "sad error")])
```
 
#### <a name="error-fx">`:log/error`
This is an effect which logs the error to the JS console.

```clojure
(ns my-district
  (:require [district.ui.logging.events]
            [re-frame.core :as re-frame]))
            
(re-frame/reg-event-fx
 ::my-event
 (fn []
   {:log/error ["Unexpected Error" {:foo "bar"} ::my-district]}))
```

## Development
```bash
lein deps

# To run tests and rerun on changes
lein doo chrome tests
```
