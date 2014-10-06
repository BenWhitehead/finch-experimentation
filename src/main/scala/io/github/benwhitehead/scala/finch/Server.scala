package io.github.benwhitehead.scala.finch

import io.github.benwhitehead.finch.SimpleFinchServer

/**
 * @author Ben Whitehead
 */
object Server extends SimpleFinchServer {
  override lazy val config = DefaultConfig.copy(port = 17070)
  def endpoint = Echo
}
