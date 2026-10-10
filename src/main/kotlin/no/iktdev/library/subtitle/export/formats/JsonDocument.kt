package no.iktdev.library.subtitle.export.formats

import com.google.gson.Gson
import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.export.Exporter
import java.io.File

class JsonDocument(folder: File, baseName: String): Exporter(folder, baseName) {
    override fun format(dialogs: List<Dialog>): List<String> {
        // DO nothing
        return listOf()
    }

    override fun write(dialogs: List<Dialog>): File {
        val file = outFile("json")
        deleteOnExistAndOverwriteAllowed(file)

        val data = Gson().toJson(dialogs)
        file.printWriter().use { out ->
            out.print(data)
        }
        return file
    }
}