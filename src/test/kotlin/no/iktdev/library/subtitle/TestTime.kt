package no.iktdev.library.subtitle

import org.junit.jupiter.api.Test
import no.iktdev.library.subtitle.classes.Time
import org.junit.jupiter.api.Assertions.assertEquals

class TestTime {

    @Test
    fun testParse() {
        val time = Time.fromAss("0:00:56.82")
        assertEquals(56, time.seconds)
        assertEquals(820, time.milliSeconds)
    }

}
