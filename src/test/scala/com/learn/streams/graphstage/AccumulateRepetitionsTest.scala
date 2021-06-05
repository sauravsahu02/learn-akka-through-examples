package com.learn.streams.graphstage

import akka.actor.ActorSystem
import akka.pattern.pipe
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestProbe
import org.scalatest.FunSuite
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Saurav Sahu on 5th June 2021
 */

class AccumulateRepetitionsTest extends FunSuite {
  implicit val actorSystem : ActorSystem = ActorSystem()
  case class Entry[T](value : T)

  test("Grouping Repeated Items Together - Int items") {
    val inputSeq = Seq(100, 100, 100, 2, 2, 300, 2, 4, 500, 500).map(Entry(_)).toIndexedSeq

    val probe = TestProbe()

    Source(inputSeq)
      .via(new AccumulateRepetitions(_.value))
      .runWith(Sink.seq)
      .pipeTo(probe.ref)

    probe.expectMsg(Seq(Seq(Entry(100), Entry(100), Entry(100)),
                        Seq(Entry(2), Entry(2)),
                        Seq(Entry(300)),
                        Seq(Entry(2)),
                        Seq(Entry(4)),
                        Seq(Entry(500), Entry(500)))
    )
  }
  test("Grouping Repeated Items Together - String items") {
    val inputSeq = Seq("foo", "foo", "foo", "hello", "bar", "bar", "world").map(Entry(_))

    val probe = TestProbe()

    Source(inputSeq)
      .via(new AccumulateRepetitions(_.value))
      .runWith(Sink.seq)
      .pipeTo(probe.ref)

    probe.expectMsg(Seq(Seq(Entry("foo"), Entry("foo"), Entry("foo")),
      Seq(Entry("hello")),
      Seq(Entry("bar"), Entry("bar")),
      Seq(Entry("world"))))
  }
}