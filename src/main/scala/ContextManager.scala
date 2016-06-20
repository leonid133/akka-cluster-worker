package io.hydrosphere.mist

import akka.actor.Actor
import org.apache.spark.{SparkConf, SparkContext}

/** Manages context repository */
class ContextManager extends Actor {
  override def receive: Receive = {
    //case Message(addr, msg) => println(addr, msg)
    case Message(addr, msg) => {
      println(addr, msg)
      val sparkConf = new SparkConf()
        .setMaster("local[*]")
        .setAppName("foo")
        .set("spark.driver.allowMultipleContexts", "true")

      lazy val sc = new SparkContext(sparkConf)
      println(sc.startTime.toString)
      sender ! sc.startTime.toString
    }
    case _ => println("************************")
  }
}