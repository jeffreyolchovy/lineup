#!/bin/bash

PROJECT_ROOT='../..'
PROJECT_EXAMPLES='..'
PROJECT_API_EXAMPLES=$(pwd)
PROJECT_VERSION='0.1.0'

JETTY_VERSION='7.6.5.v20120716'

install() {
  if [ -d "jetty" ] && [ "`cat jetty/VERSION`" == $JETTY_VERSION ]; then
    echo 'Jetty already installed'
  else
    echo "Installing Jetty ${JETTY_VERSION}"

    if [ -d 'jetty' ]; then
      rm -rf jetty
    fi
  
    curl -o jetty.tar.gz "http://download.eclipse.org/jetty/stable-7/dist/jetty-distribution-${JETTY_VERSION}.tar.gz"
    tar -xf jetty.tar.gz

    rm jetty.tar.gz
    mv "jetty-distribution-${JETTY_VERSION}" jetty

    echo $JETTY_VERSION > jetty/VERSION

    rm -rf jetty/contexts/*
    rm -rf jetty/webapps
  fi
}

build() {
  cd $PROJECT_ROOT && sbt package && cd $PROJECT_API_EXAMPLES
}

start() {
  if [ -L jetty/webapps ]; then
    rm jetty/webapps
  else
    rm -rf jetty/webapps
  fi

  mkdir jetty/webapps

  cp "${PROJECT_ROOT}/target/scala-2.9.1/lineup_2.9.1-${PROJECT_VERSION}.war" jetty/webapps/ROOT.war

  cd jetty
  CMD="java -jar start.jar"
  nohup $CMD &> server.log &
  echo $! > jetty.pid
  cd $PROJECT_API_EXAMPLES
}

stop() {
  kill $(cat jetty/jetty.pid)
}
