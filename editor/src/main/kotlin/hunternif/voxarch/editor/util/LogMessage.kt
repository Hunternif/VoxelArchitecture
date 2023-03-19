package hunternif.voxarch.editor.util

import java.util.Date

sealed class LogMessage(
    /** Message to display in logs or status bar */
    val msg: String,
    val time: Date,
) {
    class Warning(msg: String, time: Date = Date())
        : LogMessage("Warning: $msg", time)

    class Error(val ex: Exception, time: Date = Date())
        : LogMessage("Error: ${ex.javaClass.simpleName}: ${ex.message}", time)
}

