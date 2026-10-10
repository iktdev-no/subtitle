package no.iktdev.library.subtitle.reader

import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.cleaner.scrubNoisyCharacters
import no.iktdev.library.subtitle.cleaner.scrubSlashers
import java.io.File

abstract class BaseReader(file: File? = null) {
    private val lines: MutableList<String> = mutableListOf()
    fun getLines(): List<String> {
        return lines
    }

    init {
        if (file != null) {
            val _lines = file.readLines(charset = Charsets.UTF_8)
                .map {
                    scrubSlashers(it)
                }.map {
                    scrubNoisyCharacters(it)
                }
            lines.addAll(_lines)
        }
    }


    abstract fun read(): List<Dialog>

}