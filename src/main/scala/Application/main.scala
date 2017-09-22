package main

import BittrexApiBinding.{BittrexQueryActor, Query}
import akka.actor.{ActorSystem, Props}

object main extends App{
  override def main(args: Array[String]): Unit = {
    val system = ActorSystem("akkaBittrexTrade")
    val queryActor = system.actorOf(Props[BittrexQueryActor])
    queryActor ! Query("getbalances", Map())
  }
}