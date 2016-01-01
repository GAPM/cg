package grpc
package lib
package util

object Logger {
  type LogLevel = LogLevel.Value
  object LogLevel extends Enumeration {
    final val ERROR = Value
    final val DEBUG = Value
  }

  private var maxLevel: LogLevel = LogLevel.ERROR

  def setMaxLevel(level: LogLevel) {
    maxLevel = level
  }

  def log(msg: String, level: LogLevel): Unit = {
    if (level <= maxLevel) {
      println(s"$level: $msg")
    }
  }
}
