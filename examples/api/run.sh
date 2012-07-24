#!/bin/bash

. ./jetty.sh

PROJECT_ROOT='../..'
PROJECT_EXAMPLES='..'
PROJECT_API_EXAMPLES=$(pwd)
PROJECT_VERSION='0.1.0'
SCALA_VERSION='2.9.1'

build() {
  cd $PROJECT_ROOT && sbt package && cd $PROJECT_API_EXAMPLES
}

install && build
start "${PROJECT_ROOT}/target/scala-${SCALA_VERSION}/lineup_${SCALA_VERSION}-${PROJECT_VERSION}.war" && sleep 10

curl -X POST \
     -H 'Content-type:application/json' \
     -d "{\"strategy\":$(cat ../data/strategy.json), \"players\":$(cat ../data/players.json)}" \
     'http://localhost:8080/lineup'

curl -X POST \
     -H 'Content-type:application/json' \
     -d "{\"strategy\":$(cat ../data/strategy.json), \"players\":$(cat ../data/lineup.json)}" \
     'http://localhost:8080/fitness'

stop
exit 0 
