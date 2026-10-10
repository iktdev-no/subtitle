package no.iktdev.library.subtitle.export

import no.iktdev.library.subtitle.Configuration
import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.export.formats.JsonDocument
import no.iktdev.library.subtitle.export.formats.Subrip
import no.iktdev.library.subtitle.export.formats.SynchronizedAccessibleMediaInterchange
import no.iktdev.library.subtitle.export.formats.VideoTextTracks
import java.io.File

/**
 * @param sourceFile File the dialogs are created from
 */
class Export(val sourceFile: File, val folder: File, val baseName: String) {

    fun write(dialogs: List<Dialog>): MutableList<File> {
        val writtenFiles: MutableList<File> = mutableListOf()
        val ext = sourceFile.extension
        if (!ext.contains("srt", true))
            writtenFiles.add(writeSrt(dialogs))
        if (!ext.contains("vtt", true))
            writtenFiles.add(writeVtt(dialogs))
        if (!ext.contains("smi", true) && !ext.contains("sami", true))
            writtenFiles.add(writeSmi(dialogs))
        if (Configuration.exportJson) {
            JsonDocument(folder = folder, baseName = baseName).write(dialogs)
        }
        return writtenFiles
    }

    fun writeSrt(dialogs: List<Dialog>): File {
        return Subrip(folder = folder, baseName = baseName).write(dialogs)
    }
    fun writeVtt(dialogs: List<Dialog>,): File {
        return VideoTextTracks(folder = folder, baseName = baseName).write(dialogs)
    }
    fun writeSmi(dialogs: List<Dialog>,): File {
        val smi = SynchronizedAccessibleMediaInterchange(folder = folder, baseName = baseName)
        return smi.write(dialogs)
    }



}