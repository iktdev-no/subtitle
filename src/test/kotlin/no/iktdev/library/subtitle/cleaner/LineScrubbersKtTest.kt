package no.iktdev.library.subtitle.cleaner

import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class LineScrubbersKtTest {

    @Test
    fun removeTags() {
        val tekst = "Name:\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\h\\hUnspecified"
        val scr = scrubSlashers(tekst)
        assertEquals("Name: Unspecified", scr)
    }

}