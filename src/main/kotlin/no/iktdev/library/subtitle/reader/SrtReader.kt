package no.iktdev.library.subtitle.reader

import no.iktdev.library.subtitle.Configuration
import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.classes.DialogType
import no.iktdev.library.subtitle.classes.SubripData
import no.iktdev.library.subtitle.classes.Time
import java.io.File

class SrtReader(file: File? = null): BaseReader(file) {

    fun isNumberLine(line: String): Boolean {
        val lineNumberRegex = Regex("^[0-9]*\$")
        return lineNumberRegex.containsMatchIn(line)
    }

    fun isTimeLine(line: String): Boolean {
        val timeRegex = Regex("\\d{2}:\\d{2}:\\d{2},\\d{3}\\s-->\\s\\d{2}:\\d{2}:\\d{2},\\d{3}")
        return timeRegex.containsMatchIn(line)
    }

    fun isSpaceLine(line: String): Boolean {
        return line.isBlank()
    }

    fun group(lines: List<String>): MutableList<List<String>> {
        val grouped: MutableList<List<String>> = mutableListOf()
        val cache = mutableListOf<String>()

        lines.forEach {
            if (it.isNotEmpty() && it.isNotBlank())
                cache.add(it)

            if (it.isEmpty() || it.isBlank() || lines.last() == it) {
                grouped.add(cache.toList())
                cache.clear()
            }
        }
        return grouped
    }

    fun deserialize(group: List<String>): SubripData? {
        if (group.size < 3) {
            return null
        }
        val number = group[0].also {
            if (!isNumberLine(it) || it.isBlank()) {
                return null
            }
        }
        val timeLine = group[1].also {
            if (!isTimeLine(it) || it.isBlank()) {
                return null
            }
        }

        val textLines = group.subList(2, group.size).also {
            if (it.isEmpty()) {
                return null
            }
        }

        return SubripData(
            number,
            timeLine,
            textLines.toMutableList()
        )
    }

    fun transform(it: SubripData): Dialog {
        val timeLines = it.time.split("-->")
        val start = timeLines.first().trim()
        val end = timeLines.last().trim()
        var text = it.text.joinToString(Configuration.carriageReturn)
        return Dialog(start = Time.fromSrt(start), end = Time.fromSrt(end), text = text, type = DialogType.DIALOG_NORMAL)
    }


    override fun read(): List<Dialog> {
        val srts = group(getLines()).mapNotNull { deserialize(it) }
        return srts.map { transform(it) }
    }

}