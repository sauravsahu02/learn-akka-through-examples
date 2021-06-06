package com.learn.actorsystem

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

import scala.collection.mutable.ListBuffer

sealed class QueryElem
case class Select(fields: List[String]) extends QueryElem
case class Filter(condition: EmployeeRecord => Boolean) extends QueryElem

case class Query(records: Seq[EmployeeRecord], queryElements: Seq[QueryElem])
case class FinalResult(records : Seq[EmployeeRecord])

// Communication : QueryEngine -> FilterPerformer -> SelectPerformer -> Dashboard

object Dashboard{
  def apply(): Behavior[FinalResult] = Behaviors.receive{
    (context, msg) =>
      msg.records.foreach(r => context.log.info(r.toString))
      Behaviors.same
  }
}

object FilterPerformer{
  case class FilterQuery(query: Query, replyTo: ActorRef[Query])

  def apply(): Behavior[Query] = Behaviors.receive {
    (context, msg) =>
      val selectedEmployeeRecords: ListBuffer[EmployeeRecord] = ListBuffer.empty
      msg.records.foreach {
        employeeRecord =>
          msg.queryElements.foreach {
            case Filter(condition) => if (condition(employeeRecord)) selectedEmployeeRecords += employeeRecord
            case _ =>
          }
      }

      val queryEngineSelectActor : ActorSystem[Query] = ActorSystem(SelectPerformer(), "select")
      queryEngineSelectActor ! Query(selectedEmployeeRecords.toIndexedSeq, msg.queryElements)
      Behaviors.same
  }
}
object SelectPerformer {
  def apply(): Behavior[Query] = Behaviors.receive {
    (context, msg) =>
      val selectedEmployeeRecords: ListBuffer[EmployeeRecord] = ListBuffer.empty
      msg.records.foreach {
        employeeRecord =>
          var empRecOpt: Option[EmployeeRecord] = None
          msg.queryElements.foreach {
              case Select(fields) => fields.foreach {
                case "name" => {
                  if (empRecOpt.isEmpty) empRecOpt = Some(new EmployeeRecord(name = employeeRecord.name))
                  else empRecOpt.foreach(empRec => empRec.name = employeeRecord.name)
                }
                case "age" => {
                  if (empRecOpt.isEmpty) empRecOpt = Some(new EmployeeRecord(age = employeeRecord.age))
                  else empRecOpt.foreach(empRec => empRec.age = employeeRecord.age)
                }
                case "gender" => {
                  if (empRecOpt.isEmpty) empRecOpt = Some(new EmployeeRecord(name = employeeRecord.gender))
                  else empRecOpt.foreach(empRec => empRec.gender = employeeRecord.gender)
                }
                case unknownField => context.log.error(s"The field $unknownField doesn't exist in employee table")
            }
              case _ =>
          }
          empRecOpt.foreach {
            empRec => selectedEmployeeRecords += empRec
          }
      }

      val dashboardActor: ActorSystem[FinalResult] = ActorSystem(Dashboard(), "dashboard_report")
      dashboardActor ! FinalResult(selectedEmployeeRecords.toIndexedSeq)
      Behaviors.same
  }
}

class EmployeeRecord(var name: Option[String] = None, var age: Option[Int] = None, var gender: Option[String] = None) {
  override def toString: String = {
    f"Employee:" +
      f" ${name.map(v => f"name=$v").getOrElse("")}" +
      f" ${age.map(v => f"age=$v").getOrElse("")}" +
      f" ${gender.map(v => f"gender=$v").getOrElse("")}"
  }
}

object EmployeeRecord{
  def ageGreaterThanEqual(minAge : Int) = (e: EmployeeRecord) => e.age.getOrElse(0) >= minAge
}

object QueryEngine{
  val queryEngineFilterActor : ActorSystem[Query] = ActorSystem(FilterPerformer(), "filter")

  def apply(): Behavior[Query] = Behaviors.receive{
    (context, msg) => {
      context.log.info(f"Number of records originally: ${msg.records.length}")
      queryEngineFilterActor ! msg
    }
    Behaviors.same
  }
}

object Demo{
  def main(args: Array[String]): Unit = {
    val rec1 = new EmployeeRecord(Some("Foo"), Some(25), Some("Male"))
    val rec2 = new EmployeeRecord(Some("Bar"), Some(30), Some("Female"))
    val rec3 = new EmployeeRecord(Some("Saw"), Some(32), Some("Female"))
    val rec4 = new EmployeeRecord(Some("Koo"), Some(28), Some("Male"))
    val records = List(rec1, rec2, rec3, rec4)
    val basicSelectFilterQuery = List(Select(List("age", "name")),
      Filter(EmployeeRecord.ageGreaterThanEqual(28)))
    executeQuery(Query(records, basicSelectFilterQuery))
  }
  def executeQuery(query: Query): Unit = {
    val queryEngineActor : ActorSystem[Query] = ActorSystem(QueryEngine(), "query-planner")
    queryEngineActor ! query
  }
}

