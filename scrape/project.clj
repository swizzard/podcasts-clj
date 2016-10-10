(defproject scrape "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.391"]
                 [aleph "0.4.1"]
                 [cheshire "5.6.3"]
                 [clj-time "0.12.0"]
                 [com.datomic/datomic-free "0.9.5404"]
                 [enlive "1.1.6"]
                 [funcool/httpurr "0.6.2"]]
  :main ^:skip-aot scrape.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :jvm-opts ["-XX:-OmitStackTraceInFastThrow"]
  ; :jvm-opts ["-Dhttps.protocols=TLSv1.2"
  ;            ; "-Dhttps.cipherSuites=TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA256,TLS_ECDHA_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"
  ;            "-Djavax.net.ssl.trustStore=/Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home/jre/lib/security/cacerts"
; ]             ; "-Djavax.net.debug=all"]
  )
