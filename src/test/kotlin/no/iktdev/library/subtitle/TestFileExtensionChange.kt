package no.iktdev.library.subtitle

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

class TestFileExtensionChange {

    @Test
    fun testSrt() {
        val outFile = File("/tmp/test/file/sample-subtitle.ass")
        val fullpath = outFile.absolutePath
        val srtFile = fullpath.replaceAfterLast(".", "srt")
        assertEquals("/tmp/test/file/sample-subtitle.srt", srtFile)
    }

    @Test
    fun extensionComparison() {
        val ext =  File("/tmp/test/file/sample-subtitle.ass").extension
        assertEquals(ext, "ass")
        assertDoesNotThrow {
            if (!ext.contains("ass", true)) {
                throw RuntimeException("Not supposed to end up here")
            }

        }
    }
}
