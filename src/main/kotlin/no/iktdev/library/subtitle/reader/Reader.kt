package no.iktdev.library.subtitle.reader

import java.io.File

class Reader(val file: File) {

    fun getSubtitleReader(): BaseReader? {
        if (!file.exists())
            return null
        return when (file.extension) {
            "ass" -> AssReader(file)
            "srt" -> SrtReader(file)
            else -> null
        }
    }

}