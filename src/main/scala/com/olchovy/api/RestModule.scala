package com.olchovy.api

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import util.Random
import cc.spray._
import cc.spray.json._
import cc.spray.typeconversion.SprayJsonSupport._


trait RestModule extends Directives
{
  import DefaultJsonProtocol._

  val service = {
    path("") {
      get { _.complete(Map("response" → "Not yet implemented")) }
    }
  }
}

