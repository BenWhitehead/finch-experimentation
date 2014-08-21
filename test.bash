#!/bin/bash
set -o errexit -o nounset -o pipefail

PORT=${PORT:-7070}
CORE_COUNT=${CORE_COUNT:-8}

function startServer {
  java -jar finch-experimentation-assembly-0.1-SNAPSHOT.jar 7070
}

function beforeTest {(
  echo "beforeTest"
  rm -rf test || true
  mkdir test
  cd test
  cp ../target/scala-2.10/finch-experimentation-assembly-*.jar .
  cp ../src/main/resources/logback*.xml .
  cp ../install-wrk.sh .
  ./install-wrk.sh
)}

function burnin {
  wrk $@ 2>1 > /dev/null
}
function wrk {
  wrk/wrk -t$(($CORE_COUNT * 2)) -c$(($CORE_COUNT * 50)) -d${1:-30}s http://${2:-localhost:7070}/echo/test
}
function runTest {(
  cd test
  (java -classpath finch-experimentation-assembly-*.jar io.github.benwhitehead.scala.finch.AccessLogServer ${PORT} 2>stderr1 >stdout1 ) &
  pid=$!
  sleep 5 # wait for server to start up
  burnin 15
  wrk 60

  kill -TERM ${pid}
)}

function main {
  echo "main"
#  beforeTest
  runTest
}

##################################################################### Utilities
function trim {
    local var=$@
    var="${var#"${var%%[![:space:]]*}"}"   # remove leading whitespace characters
    var="${var%"${var##*[![:space:]]}"}"   # remove trailing whitespace characters
    echo -n "$var"
}
function now { date +"%Y-%m-%d %H:%M:%S" | tr -d '\n' ;}
function msg { println "$*" >&2 ;}
function err { local x=$? ; msg "$*" ; return $(( $x == 0 ? 1 : $x )) ;}
function println { printf '%s\n' "$(now) $*" ;}
function print { printf '%s ' "$(now) $*" ;}

######################### Delegates to subcommands or runs main, as appropriate
if [[ ${1:-} ]] && declare -F | cut -d' ' -f3 | fgrep -qx -- "${1:-}"
then "$@"
else main "$@"
fi
