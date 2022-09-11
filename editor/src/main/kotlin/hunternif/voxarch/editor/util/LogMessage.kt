package hunternif.voxarch.editor.util

import java.util.Date

sealed class LogMessage(val time: Date) {
    class Warning(val msg: String, time: Date = Date()) : LogMessage(time)
    class Error(val ex: Exception, time: Date = Date()) : LogMessage(time)
}

