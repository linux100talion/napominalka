package com.napominalka.log

import android.content.Context
import com.napominalka.engine.Cause
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Текстовый лог. SPEC 7.1–7.3.
 * Сейчас пишет в Android/data/<pkg>/files/Napominalka/. Перенос в Documents/Napominalka
 * через MediaStore: TODO.
 */
class EventLog(context: Context) {
    private val root = File(context.getExternalFilesDir(null) ?: context.filesDir, "Napominalka")
    val logsDir = File(root, "logs")
    val blockersFile = File(root, "blockers.log")

    private val ts = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /** cycleDate: дата начала текущего цикла, имя файла лога. */
    fun write(cycleDate: LocalDate, event: String, vararg fields: String) {
        val line = buildString {
            append(LocalDateTime.now().format(ts)).append(" | ")
            append(event.padEnd(16)).append(" | ")
            append(fields.joinToString(" | "))
        }
        append(File(logsDir, "$cycleDate.log"), line)
    }

    fun blocker(cycleDate: LocalDate, isStep: Boolean, id: String, cause: Cause, path: List<String>, reason: String) {
        val kind = (if (isStep) "" else "PROC ") + cause.name
        val line = "${LocalDateTime.now().format(ts)} | $kind | cycle=$cycleDate | $id | path=${path.joinToString(">")} | ${reason.replace('\n', ' ')}"
        append(blockersFile, line)
    }

    fun listFiles(): List<File> {
        val files = ArrayList<File>()
        if (blockersFile.exists()) files += blockersFile
        logsDir.listFiles()?.sortedByDescending { it.name }?.let { files += it }
        return files
    }

    private fun append(file: File, line: String) {
        try {
            file.parentFile?.mkdirs()
            file.appendText(line + "\n", Charsets.UTF_8)
        } catch (_: Exception) {
            // Лог не должен ронять приложение. TODO: показать ошибку на главном экране.
        }
    }
}
