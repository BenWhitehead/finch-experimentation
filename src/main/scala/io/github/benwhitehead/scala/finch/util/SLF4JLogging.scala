package io.github.benwhitehead.scala.finch.util

import org.slf4j.bridge.SLF4JBridgeHandler

import java.util.logging.{Level, LogManager}

/**
 * @author Ben Whitehead
 */
object SLF4JLogging {

  def install(): Unit = {
    // Turn off Java util logging so that slf4j can configure it
    LogManager.getLogManager.getLogger("").getHandlers.toList.map { l =>
      l.setLevel(Level.OFF)
    }
    SLF4JBridgeHandler.install()
  }

  def uninstall(): Unit = {
    SLF4JBridgeHandler.uninstall()
  }

  def apply(f: => Unit): Unit = {
    install()
    try {
      f
    }
    finally {
      uninstall()
    }
  }
}
