package com.olchovy.lineup.serde

/*
import spray.json._
import com.olchovy.lineup.{Lineup, Strategy}

object ApiJsonProtocol extends DefaultJsonProtocol {

  implicit object RequestJsonFormat extends RootJsonFormat[(Strategy, Lineup)] {
    def read(json: JsValue): (Strategy, Lineup) = {
      json.asJsObject.getFields("strategy", "players") match {
        case Seq(strategy, players) ⇒
          (StrategyJsonFormat.read(strategy), LineupJsonFormat.read(players))
        case _ ⇒
          deserializationError("Object literal with properties 'strategy', 'players' expected")
      }
    }

    def write(tuple: (Strategy, Lineup)): JsValue = serializationError("RequestJsonFormat is read-only")
  }
}
*/
