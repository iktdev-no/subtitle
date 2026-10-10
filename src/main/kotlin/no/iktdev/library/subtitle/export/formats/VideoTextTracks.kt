package no.iktdev.library.subtitle.export.formats

import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.classes.Time
import no.iktdev.library.subtitle.export.Exporter
import java.io.File

class VideoTextTracks(folder: File, baseName: String): Exporter(folder, baseName) {

    override fun format(dialogs: List<Dialog>): List<String> {
        val formatted: MutableList<String> = mutableListOf()

        formatted.add("WEBVTT")
        formatted.add(" ")
        formatted.add(" ")

        dialogs.forEach {
            val time = "${it.start.toVtt()} --> ${it.end.toVtt()}"
            formatted.add(time)
            formatted.add(it.text)
            formatted.add("")
            formatted.add("")
        }

        // Since some devices makes the subtitle stuck on the screen after the trakc has ended
        val overflowStart = dialogs.last().end
        val overflowEnd = Time.fromMilliseconds(overflowStart.inMilli() + 2000)
        val time = "${overflowStart.toVtt()} --> ${overflowEnd.toVtt()}"
        formatted.add(time)
        formatted.add("")
        formatted.add("")
        formatted.add("")
        return formatted
    }


    override fun write(dialogs: List<Dialog>): File {
        val file = outFile("vtt")
        deleteOnExistAndOverwriteAllowed(file)
        val formatted = format(dialogs)
        file.printWriter().use { out ->
            formatted.forEach {
                out.println(it)
            }
        }
        return file
    }

}