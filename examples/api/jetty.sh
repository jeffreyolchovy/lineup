#!/bin/bash

JETTY_VERSION='7.6.5.v20120716'

install() {
  if [ -d "jetty" ] && [ "`cat jetty/VERSION`" == $JETTY_VERSION ]; then
    echo 'Jetty is already installed, skipping installation.'
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

start() {
  local WAR=$1

  if [ -L jetty/webapps ]; then
    rm jetty/webapps
  else
    rm -rf jetty/webapps
  fi

  mkdir jetty/webapps

  cp $WAR jetty/webapps/ROOT.war

  cd jetty
  CMD="java -jar start.jar"
  nohup $CMD &> server.log &
  echo $! > jetty.pid
  cd ..
}

stop() {
  kill $(cat jetty/jetty.pid)
}
