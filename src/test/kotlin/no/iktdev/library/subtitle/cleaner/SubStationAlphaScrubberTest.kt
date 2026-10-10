package no.iktdev.library.subtitle.cleaner

import no.iktdev.library.subtitle.cleaner.SubStationAlphaScrubber
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test

class SubStationAlphaScrubberTest {

    @Test
    fun textIsScrubbed() {
        val text = "{\\c&H2DFFFF&\\3c&H176EDC&\\bord2\\fs17\\an8\\pos(480,206)\\fad(1000,1)}WANNA FIGHT"
        val result = SubStationAlphaScrubber.stripTagging(text)
        val excpected = "WANNA FIGHT"
        assertEquals("Unsupported values is removed cleanly",excpected, result)
    }

}