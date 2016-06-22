package io.hydrosphere.mist

import java.util.concurrent.Executors._

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import io.hydrosphere.mist.Messages._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}



private[mist] class JobRunner extends Actor {

  // Thread context for parallel running jobs
  val executionContext = ExecutionContext.fromExecutorService(newFixedThreadPool(5)) // TODO to config MistConfig.Settings.threadNumber

  // Actor which is creates spark contexts
  lazy val contextManager: ActorRef = context.actorOf(Props[ContextManager], name = Constants.Actors.contextManagerName)

  override def receive: Receive = {
    case configuration: JobConfiguration =>
      val originalSender = sender

      // Time of spark context creating is definitely less than one day
      implicit val timeout = Timeout(1.day)

      // Request spark context creating
      val contextFuture = contextManager ? CreateContext(configuration.name)

      contextFuture.flatMap {
        case contextWrapper: ContextWrapper =>

          lazy val job = Job(configuration, contextWrapper, self.path.name)

          lazy val jobRepository = InMemoryJobRepository /*{
            MistConfig.Recovery.recoveryOn match {
              case true => RecoveryJobRepository
              case _ => InMemoryJobRepository
            }
          }*/ //TODO add config
          val future: Future[Either[Map[String, Any], String]] = Future {
            jobRepository.add(job)
            println(s"${configuration.name}#${job.id} is running")
            job.run()
          }(executionContext)
          future
            .andThen {
              case _ => {
                if (false) { //TODO add config MistConfig.Contexts.isDisposable(configuration.name)
                  contextManager ! RemoveContext(contextWrapper)
                }
                /*
                jobRepository.equals(RecoveryJobRepository) match
                {
                  case true => RecoveryJobRepository.removeFromRecovery(job)
                }*/
              }
            }(ExecutionContext.global)
            .andThen {
              case Success(result: Either[Map[String, Any], String]) => originalSender ! result
              case Failure(error: Throwable) => originalSender ! Right(error.toString)
            }(ExecutionContext.global)
      }(ExecutionContext.global)
  }
}
