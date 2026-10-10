package no.iktdev.library.subtitle.export.formats

import no.iktdev.library.subtitle.Configuration
import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.export.Exporter
import java.io.File

class Subrip(folder: File, baseName: String): Exporter(folder, baseName) {

    override fun format(dialogs: List<Dialog>): List<String> {
        val formatted: MutableList<String> = mutableListOf()
        dialogs.withIndex().forEach {
            formatted.add((it.index+1).toString()) //Adds counter to SRT
            val time = "${it.value.start.toSrt()} --> ${it.value.end.toSrt()}"
            formatted.add(time)
            formatted.add(it.value.text)
            formatted.add(Configuration.carriageReturn)
        }
        return formatted
    }


    override fun write(dialogs: List<Dialog>): File {
        val file = outFile("srt")
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