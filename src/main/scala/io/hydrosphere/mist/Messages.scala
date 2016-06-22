package io.hydrosphere.mist

import io.hydrosphere.mist.ContextWrapper

private[mist] object Messages {

  case class CreateContext(name: String)

  case class StopAllContexts()

  case class RemoveContext(context: ContextWrapper)
}