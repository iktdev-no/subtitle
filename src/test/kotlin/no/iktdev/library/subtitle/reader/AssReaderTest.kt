package no.iktdev.library.subtitle.reader

import no.iktdev.library.subtitle.Syncro
import no.iktdev.library.subtitle.SubtitleFixtures
import no.iktdev.library.subtitle.classes.DialogType
import no.iktdev.library.subtitle.classes.Time
import no.iktdev.library.subtitle.reader.AssReader
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertDoesNotThrow


class AssReaderTest {
    private val reader = AssReader(null)
    val text = "Dialogue: 0,0:00:52.11,0:00:55.65,Default,Speaker,0,0,0,,{\\i1}A fictional pilot is speaking.{\\i0}"

    @Test
    fun testIsDialog() {
        assertTrue(reader.isDialog(text))
    }

    @Test
    fun assertDeserialization() {
        val deserialized = reader.deserialize(text)
        assertNotNull(deserialized)

    }


    @Test
    fun isAbleToDecodeTime() {
        val deserialized = reader.deserialize(text) ?: throw Exception("Deserialized is null")
        assertDoesNotThrow {
            val parsed = Time.fromAss(deserialized.start)
            assertEquals(52, parsed.seconds)
            assertEquals(110, parsed.milliSeconds)
        }
    }

    @Test
    fun testSyntheticAss_ReadAndSync() {
        val lines = SubtitleFixtures.ass
        val ssad = lines.mapNotNull { reader.deserialize(it) }
        val mapped = ssad.map { reader.transform(it) }
        val synced = Syncro().sync(mapped.toList())
        assertTrue(synced.isNotEmpty())

        synced.forEach {
            assertTrue(it.start.inMilli() <= it.end.inMilli(), "${it.text} has end time after start ${it.start.inMilli()}:${it.end.inMilli()}")
        }
    }

    @Test
    fun isDialog1() {
        val line = "Dialogue: 0,0:04:11.05,0:04:15.62,Default,Speaker,0,0,0,,The map is ready, \\Nwe can leave now."
        val deserialized = reader.deserialize(line)
        val transformed = reader.transform(deserialized!!)
        assertTrue(transformed.type != DialogType.SIGN_SONG)
    }

    @Test
    fun stripsDenseInlineTagsKeepsCommaTextAndDetectsPartialItalics() {
        val line = "Dialogue: 0,0:00:01.00,0:00:02.00,Default,Speaker,0,0,0,,{\\fad(100,200)\\an8\\bord2\\fs20\\pos(12,34)\\i1}A fictional phrase, with commas.{\\i0} Normal again."

        val parsed = reader.transform(reader.deserialize(line)!!)

        assertEquals("A fictional phrase, with commas. Normal again.", parsed.text)
        assertEquals(DialogType.DIALOG_ITALIC, parsed.type)
    }

    @Test
    fun convertsAssLineBreaksAndHardSpaces() {
        val line = "Dialogue: 0,0:00:01.00,0:00:02.00,Default,Speaker,0,0,0,,First\\Nsecond\\nthird\\hword"

        val parsed = reader.transform(reader.deserialize(line)!!)

        assertEquals("First\r\nsecond\r\nthird\u00a0word", parsed.text)
    }

    @Test
    fun stripsKaraokeAndAnimatedOverridesWithoutLosingLyricText() {
        val line = "Dialogue: 0,0:00:01.00,0:00:02.00,Karaoke,Singer,0,0,0,,{\\k20}La{\\ko15\\t(0,100,\\fscx110)}la"

        val parsed = reader.transform(reader.deserialize(line)!!)

        assertEquals("Lala", parsed.text)
        assertEquals(DialogType.DIALOG_NORMAL, parsed.type)
    }

    @Test
    fun vectorDrawingEventsAreIgnoredAsNonDialogue() {
        val line = "Dialogue: 0,0:00:01.00,0:00:02.00,Sign,Sign,0,0,0,,{\\p1}m 0 0 l 50 0 l 50 88 l 0 88{\\p0}"

        val parsed = reader.transform(reader.deserialize(line)!!)

        assertTrue(parsed.ignore)
    }

}
