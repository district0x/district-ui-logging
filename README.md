# district-ui-logging

[![Build Status](https://travis-ci.org/district0x/district-ui-logging.svg?branch=master)](https://travis-ci.org/district0x/district-ui-logging)

Clojurescript [re-mount](https://github.com/district0x/d0x-INFRA/blob/master/re-mount.md) module, that takes care of setting up [timbre](https://github.com/ptaoussanis/timbre) + [cljs-devtools](https://github.com/binaryage/cljs-devtools) logging.

## Installation
Add `[district0x/district-ui-logging "1.0.0"]` into your project.clj  
Include `[district.ui.logging]` in your CLJS file, where you use `mount/start`

**Warning:** district0x modules are still in early stages, therefore API can change in a future.

## Usage
You can pass following args to logging component: 
* `:level` Min. level that should be logged

```clojure
  (ns my-district
    (:require [mount.core :as mount]
              [district.ui.logging]
              [taoensso.timbre :refer-macros [info warn error]]))

  (-> (mount/with-args
        {:logging {:level :info}})
    (mount/start))

  (info "Some info")
  ;; my-district:12 Some info

  (warn "Some warning" {:a 1})
  ;; my-district:15 Some warning {:a 1}

  (error "Some error" {:error "Bad things"})
  ;; my-district:18 Some error {:error "Bad things"}
```
## Development
```bash
lein deps

# To run tests and rerun on changes
lein doo chrome tests
```