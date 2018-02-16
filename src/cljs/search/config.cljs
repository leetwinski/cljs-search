(ns search.config)

(def debug?
  ^boolean goog.DEBUG)

(def ^:const +settings+
  (js->clj js/__ENV__ :keywordize-keys true))
