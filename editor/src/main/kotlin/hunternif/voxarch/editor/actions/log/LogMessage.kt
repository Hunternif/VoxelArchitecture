package hunternif.voxarch.editor.actions.log

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LogMessage(
    /** Message to display in logs or status bar */
    val msg: String,
    val severity: Severity,
    val time: LocalDateTime = LocalDateTime.now(),
    val exception: Exception? = null,
    val moreLines: List<String> = emptyList(),
) {
    val hasMoreLines: Boolean get() = moreLines.isNotEmpty()
    val formattedString: String by lazy {
        "${timeFormat.format(time)} $msg"
    }

    companion object {
        val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        fun info(msg: String) = LogMessage(msg, Severity.INFO)
        fun warn(msg: String) = LogMessage(msg, Severity.WARN)
        fun error(e: Exception): LogMessage {
            val msg = "Error: ${e.javaClass.simpleName}: ${e.message}"
            val lines = e.stackTrace.map { it.toString() }
            return LogMessage(msg, Severity.ERROR, LocalDateTime.now(), e, lines)
        }
    }
}

enum class Severity {
    INFO,
    WARN,
    ERROR,
}

