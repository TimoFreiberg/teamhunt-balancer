(ns teamhunt-balancer.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [teamhunt-balancer.core-test]
   [teamhunt-balancer.common-test]))

(enable-console-print!)

(doo-tests 'teamhunt-balancer.core-test
           'teamhunt-balancer.common-test)
