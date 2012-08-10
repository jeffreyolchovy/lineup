package com.olchovy.lineup.api

import akka.actor._
import cc.spray.{RootService, HttpService}


class Boot(system: ActorSystem)
{
  val restModule = new RestModule {
    implicit def actorSystem = system
  }

  val restService = system.actorOf(
    props = Props(new HttpService(restModule.service)),
    name = "rest-service"
  )

  val rootService = system.actorOf(
    props = Props(new RootService(restService)),
    name = "spray-root-service"
  )

  system.registerOnTermination {
    system.log.info("Application shut down")
  }
}
