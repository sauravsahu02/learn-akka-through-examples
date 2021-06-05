package com.learn.streams.graphstage

import scala.collection.immutable
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

/**
 * Created by Saurav Sahu on 5th June 2021
 */

final class AccumulateRepetitions[E, P] (prop: E => P)
  extends GraphStage[FlowShape[E, immutable.Seq[E]]] {
  private val in = Inlet[E]("AccumulateRepetitions.in")
  private val out = Outlet[immutable.Seq[E]]("AccumulateRepetitions.out")

  // Define Shape object which is analogous to the points that connects to the outside world.
  override def shape = FlowShape.of(in, out)

  override def createLogic(inheritedAttributes: Attributes) = new GraphStageLogic(shape) {
    var lastEntry: Option[E] = None
    val buffer = Vector.newBuilder[E]

    setHandlers(in, out, new InHandler with OutHandler {
      override def onPush(): Unit = {
        val newEntry = grab(in)
        if (lastEntry.isEmpty || lastEntry.contains(newEntry)){
          buffer += newEntry
          pull(in)
        }else{
          push(out, buffer.result())
          buffer.clear()
          buffer += newEntry
        }
        lastEntry = Some(newEntry)
      }
      override def onPull(): Unit = {
        pull(in)
      }
      override def onUpstreamFinish(): Unit = {
        val result = buffer.result()
        if (result.nonEmpty) {
          emit(out, result)
        }
        completeStage()
      }
    })

    override def postStop(): Unit = {
      buffer.clear()
    }
  }
}
