package io.hydrosphere.mist

import akka.actor._
import akka.cluster.ClusterEvent._
import akka.cluster._
import akka.event._


case class Message(sender: String, message: String)

class ClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  val serverAddress = "akka.tcp://mist-system@127.0.0.1:2551" + "/user/clusterMist"
  val serverActor = cluster.system.actorSelection(serverAddress)

  val clusterState = cluster.state

  val nodeAddress = cluster.selfAddress

  var nodeNumber: Int = 0
  // subscribe to cluster changes, re-subscribe when restart
  override def preStart() {
    cluster.subscribe(self, InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop() {
    cluster.unsubscribe(self)
  }

  def receive = LoggingReceive {
    case Message(addr, msg) => {
      (addr, msg) match {
        case (serverAddress, "Im Leader") => {
          println("Server Address:")
          println(addr)

          serverActor ! new Message(nodeAddress.toString, s"Im recieve you message, Leader. Im worker node number $nodeNumber")
        }
        case (serverAddress, _) => {
          if( msg.split('#').head == "Now you node number ") {
            println(s"Now my node number will be ${msg.split('#').last}")
            nodeNumber = msg.split('#').last.toInt
            serverActor ! new Message(nodeAddress.toString, s"Ok. Now my node number #$nodeNumber")
          }
        }
        case _ => println(msg)
      }
    }

    case MemberUp(member) => {
      log.info(s"[Listener] node is up: $member")
      if(member.address.toString == nodeAddress.toString)
        serverActor ! new Message(s"${member.address}", "Im Up")
    }

    case UnreachableMember(member) =>
      log.info(s"[Listener] node is unreachable: $member")

    case MemberRemoved(member, prevStatus) =>
      log.info(s"[Listener] node is removed: $member after $prevStatus")

    case ev: MemberEvent =>
      log.info(s"[Listener] event: $ev")
  }
}

object WorkerNode extends App {
  val system = ActorSystem("mist-system")
  val c = system.actorOf(Props[ClusterListener], "clusterMist")
  system.awaitTermination()
}
