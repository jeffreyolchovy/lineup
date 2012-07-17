package com.olchovy.api

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import util.Random
import cc.spray._
import cc.spray.json._
import cc.spray.typeconversion.SprayJsonSupport._
import com.olchovy.Strategy
import com.olchovy.domain._
import com.olchovy.io._


trait RestModule extends Directives
{
  object RestJsonProtocol extends DefaultJsonProtocol
  {
    implicit object RequestJsonFormat extends RootJsonFormat[(Strategy, Lineup)]
    {
      def read(json: JsValue): (Strategy, Lineup) = json.asJsObject.getFields("strategy", "players") match {
        case Seq(strategy, players) ⇒ (StrategyJsonFormat.read(strategy), LineupJsonFormat.read(players))
        case _ ⇒ deserializationError("Object literal with properties 'strategy', 'players' expected")
      }

      def write(tuple: (Strategy, Lineup)): JsValue = serializationError("RequestJsonFormat is read-only")
    }
  }

  val service = {
    import RestJsonProtocol._

    path("lineup") {
      post {
        content(as[(Strategy, Lineup)]) {
          case (strategy, roster) ⇒
            val lineups = strategy.execute(roster.players).map { lineup ⇒
              (strategy.fitness(lineup), lineup.players.map(_.name))
            }.toSeq.sortWith { (a, b) ⇒ 
              a._1 > b._1
            }.map(_._2)

            _.complete(lineups)
        }
      }
    } ~
    path("fitness") {
      post {
        content(as[(Strategy, Lineup)]) {
          case (strategy, lineup) ⇒ _.complete(Map("score" → strategy.fitness(lineup)))
        }
      }
    }
  }
}

