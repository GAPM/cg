package grpc.lib.util

object Logger {
    private var _maxLevel = LogLevel.ERROR

    fun setMaxLevel(level: LogLevel) {
        _maxLevel = level
    }

    fun log(msg: String?, level: LogLevel) {
        if (level <= _maxLevel) {
            when (level) {
                LogLevel.ERROR -> System.err.println(msg)
                else -> println(msg)
            }
        }
    }
}