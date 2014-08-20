package io.github.benwhitehead.scala.finch

import io.github.benwhitehead.scala.finch.util.SLF4JLogging

/**
 * @author Ben Whitehead
 */
object Main {
  def main(args: Array[String]) {
    SLF4JLogging {
      Server.main(args)
    }
  }
}
