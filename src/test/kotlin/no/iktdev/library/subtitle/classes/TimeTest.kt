package no.iktdev.library.subtitle.classes

import org.junit.Test
import org.junit.jupiter.api.Assertions.*

internal class TimeTest {

    @Test
    fun testFromAssToSrt1() {
        val leadingZero = "0:00:14.04"
        val testTime2 = "0:00:17.25"

        val time = Time.fromAss(leadingZero)
        assertEquals("00:00:14,040", time.toSrt())
    }

    @Test
    fun testFromAssToSrt2() {
        val leadingZero = "0:00:04.04"
        val testTime2 = "0:00:17.25"

        val time = Time.fromAss(leadingZero)
        assertEquals("00:00:04,040", time.toSrt())

    }

    @Test
    fun testFromSrtToSrt1() {
        val leadingZero = "00:00:14,040"
        val time = Time.fromSrt(leadingZero)
        assertEquals("00:00:14,040", time.toSrt())
    }

    @Test
    fun testFromSrtToSrt2() {
        val leadingZero = "00:00:14,004"
        val time = Time.fromSrt(leadingZero)
        assertEquals("00:00:14,004", time.toSrt())
    }

    @Test
    fun testFromSrtToSrt3() {
        val leadingZero = "00:00:04,004"
        val time = Time.fromSrt(leadingZero)
        assertEquals("00:00:04,004", time.toSrt())
    }




}