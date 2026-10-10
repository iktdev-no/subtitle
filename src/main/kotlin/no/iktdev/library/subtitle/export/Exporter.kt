package no.iktdev.library.subtitle.export

import no.iktdev.library.subtitle.Configuration
import no.iktdev.library.subtitle.classes.Dialog
import java.io.File

abstract class Exporter(val folder: File, val baseName: String) {

    fun outFile(extension: String): File {
        return File(folder, "$baseName.$extension")
    }

    abstract fun format(dialogs: List<Dialog>): List<String>
    abstract fun write(dialogs: List<Dialog>): File

    fun deleteOnExistAndOverwriteAllowed(file: File) {
        if (file.exists() && Configuration.allowOverwrite) {
            file.delete()
        }
    }

}