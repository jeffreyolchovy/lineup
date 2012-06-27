package com.olchovy.api

import akka.actor._
import cc.spray.{RootService, HttpService}


class Boot(system: ActorSystem)
{
  val mainModule = new RestService {
    implicit def actorSystem = system
  }

  val restService = system.actorOf(
    props = Props(new HttpService(mainModule.restService)),
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
