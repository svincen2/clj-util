(defproject org.clojars.sean-vincent/clj-util "0.3.3"
  :description "Some useful Clojure stuff"
  :url "https://github.com/svincen2/clj-util"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  ;; Use Clojure CLI for dependencies
  :plugins [[lein-tools-deps "0.4.5"]]
  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]
  :lein-tools-deps/config {:config-files [:install :user :project]}

  :repl-options {:init-ns clj-util.core})
