package com.learn.scheduler

import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

case class Toggle()
case class CrossRoad(id : Int)
class TrafficLight(nCrossRoads : Int) extends Actor {
  val crossRoads = (0 until nCrossRoads).map(id => CrossRoad(id)).toList
  var currentOpenRoadId = 0
  override def receive: Receive = {
    case Toggle() => {
      currentOpenRoadId %= crossRoads.size
      displayLightsForRoad(currentOpenRoadId)
      currentOpenRoadId += 1
      println()
    }
  }
  private def displayLightsForRoad(currentOpenRoadId : Int) = {
    for(i <- 0 until crossRoads.size) {
      if (i == currentOpenRoadId)
        print("GREEN ")
      else
        print("Red   ")
    }
  }

}
object TrafficLightSimulator extends App{
  val system = ActorSystem("MySystem")
  val trafficLightActor = system.actorOf(Props(new TrafficLight(4)))
  system.scheduler.scheduleWithFixedDelay(0 seconds, 2 seconds)(() => {
    trafficLightActor ! Toggle()
  })
  // OUTPUT:
  //GREEN Red   Red   Red
  //Red   GREEN Red   Red
  //Red   Red   GREEN Red
  //Red   Red   Red   GREEN
  //GREEN Red   Red   Red
  //... so on ...
}
