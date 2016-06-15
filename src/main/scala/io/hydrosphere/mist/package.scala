package io.hydrosphere

import akka.util.Timeout
import scala.concurrent.duration._

package object mist {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext = global
}
