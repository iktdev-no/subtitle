package no.iktdev.library.subtitle.reader

import no.iktdev.library.subtitle.Configuration
import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.classes.DialogType
import no.iktdev.library.subtitle.classes.SubstationAlphaDialogData
import no.iktdev.library.subtitle.classes.Time
import no.iktdev.library.subtitle.cleaner.SubStationAlphaScrubber
import java.io.File

open class AssReader(file: File? = null): BaseReader(file) {

    fun isItalic(value: SubstationAlphaDialogData): Boolean {
        return hasOverride(value.text, 'i')
    }

    fun isBold(value: SubstationAlphaDialogData): Boolean {
        return hasOverride(value.text, 'b')
    }

    private fun hasOverride(text: String, command: Char): Boolean {
        // ASS allows multiple commands in one block, e.g. {\\an8\\i1}.
        val commandRegex = Regex("\\\\${command}([01])", RegexOption.IGNORE_CASE)
        return SubStationAlphaScrubberBlocks.findAll(text)
            .flatMap { commandRegex.findAll(it.value) }
            .any { it.groupValues[1] == "1" }
    }

    fun isSignOrSong(value: SubstationAlphaDialogData): Boolean {
        val marker = Regex("(?i)(^|[^a-z])(op|ed|sign|song)([^a-z]|$)")
        return marker.containsMatchIn(value.layer) || marker.containsMatchIn(value.style) ||
                marker.containsMatchIn(value.name)
    }

    fun isDialog(line: String): Boolean {
        return line.trimStart().startsWith("Dialogue:", ignoreCase = true) ||
                line.trimStart().startsWith("Dialog:", ignoreCase = true)
    }

    fun applyCorrectNewLine(line: String): String {
        return line.replace(Regex("\\\\[Nn]"), Configuration.carriageReturn)
            .replace("\\h", "\u00a0")
    }

    fun deserialize(line: String): SubstationAlphaDialogData? {
        if (!isDialog(line))
            return null
        val dialog = line.substringAfter(':', "")
        val segment = dialog.split(',', limit = 10)
        if (segment.size != 10)
            return null
        return SubstationAlphaDialogData(
            layer = segment[0],
            start = segment[1],
            end = segment[2],
            style = segment[3],
            name = segment[4],
            marginL = segment[5],
            marginR = segment[6],
            marginV = segment[7],
            effect = segment[8],
            text = segment[9]
        )
    }


    fun transform(it: SubstationAlphaDialogData): Dialog {
        val text = applyCorrectNewLine(SubStationAlphaScrubber.stripTagging(it.text))


        return Dialog(
            start = Time.fromAss(it.start),
            end = Time.fromAss(it.end),
            text = text,
            type = if (isSignOrSong(it))
                DialogType.SIGN_SONG
            else if (isItalic(it))
                DialogType.DIALOG_ITALIC
            else if (isBold(it))
                DialogType.DIALOG_BOLD
            else
                DialogType.DIALOG_NORMAL,
            ignore = shouldIgnore(text)
        )
    }

    fun shouldIgnore(text: String): Boolean {
        val regex = "(?i)[a-z]\\s(?:-?\\d+\\.?\\d*\\s){1,2}(?:l\\s-?\\d+\\.?\\d*\\s-?\\d+\\.?\\d*\\s?)*"
        return Regex(regex).matches(text)
    }

    override fun read(): List<Dialog> {
        val ssad = getLines().mapNotNull { deserialize(it) }
        return ssad.map { transform(it) }
    }

    private companion object {
        // Keep classification limited to ASS override blocks so ordinary text such
        // as "I" or "bold" cannot be mistaken for a formatting command.
        val SubStationAlphaScrubberBlocks = Regex("\\{[^}]*}")
    }
}
