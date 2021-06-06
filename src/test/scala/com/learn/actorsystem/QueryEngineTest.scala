package com.learn.actorsystem

import org.scalatest.FunSuite

/**
 * Created by Saurav Sahu on 6th June 2021
 */

class QueryEngineTest extends FunSuite {
  test("SELECT and FILTER based query") {
    val rec1 = new EmployeeRecord(Some("Foo"), Some(25), Some("Male"))
    val rec2 = new EmployeeRecord(Some("Bar"), Some(30), Some("Female"))
    val rec3 = new EmployeeRecord(Some("Saw"), Some(32), Some("Female"))
    val rec4 = new EmployeeRecord(Some("Koo"), Some(28), Some("Male"))

    val records = List(rec1, rec2, rec3, rec4)
    val basicSelectFilterQuery = List(Select(List("age", "name")),
      Filter(EmployeeRecord.ageGreaterThanEqual(28)))
    Demo.executeQuery(Query(records, basicSelectFilterQuery))
    // TODO : Verify the response at Dashboard side
  }
  test("SELECT only based query") {
    val rec1 = new EmployeeRecord(Some("Foo"), Some(25), Some("Male"))
    val rec2 = new EmployeeRecord(Some("Bar"), Some(30), Some("Female"))
    val rec3 = new EmployeeRecord(Some("Saw"), Some(32), Some("Female"))
    val rec4 = new EmployeeRecord(Some("Koo"), Some(28), Some("Male"))

    val records = List(rec1, rec2, rec3, rec4)
    val basicSelectFilterQuery = List(Select(List("age")))
    Demo.executeQuery(Query(records, basicSelectFilterQuery))
    // TODO : Verify the response at Dashboard side
  }
  test("FILTER only based query") {
    val rec1 = new EmployeeRecord(Some("Foo"), Some(25), Some("Male"))
    val rec2 = new EmployeeRecord(Some("Bar"), Some(30), Some("Female"))
    val rec3 = new EmployeeRecord(Some("Saw"), Some(32), Some("Female"))
    val rec4 = new EmployeeRecord(Some("Koo"), Some(28), Some("Male"))

    val records = List(rec1, rec2, rec3, rec4)
    val basicSelectFilterQuery = List(Filter(EmployeeRecord.ageGreaterThanEqual(30)))
    Demo.executeQuery(Query(records, basicSelectFilterQuery))
    // TODO : Verify the response at Dashboard side
  }
}
