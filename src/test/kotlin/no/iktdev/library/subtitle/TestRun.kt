package no.iktdev.library.subtitle

import no.iktdev.library.subtitle.classes.DialogType
import no.iktdev.library.subtitle.reader.AssReader
import no.iktdev.library.subtitle.reader.SrtReader
import org.junit.Assert.*
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat

class TestRun {

    val reader = AssReader()

    private fun hasDuplicates(items: List<String>): Boolean {
        return items.size != items.distinct().size
    }

    @Test
    fun testSyntheticAssSync() {
        val reader = AssReader();
        val ssad = SubtitleFixtures.ass.mapNotNull { reader.deserialize(it) }
        val mapped = ssad.map { reader.transform(it) }
        val synced = Syncro().sync(mapped)

        synced.forEach {
            val lines = it.text.split(Configuration.carriageReturn)
            assert(!hasDuplicates(lines)) {
                "Duplicates found with \r\n" + it.text
            }
        }
    }

    @Test
    fun testShouldIgnore1() {
        assertTrue(reader.shouldIgnore("m 0 0 l 50 0 l 50 88 l 0 88"))
    }

    @Test
    fun testTransform() {
        val text = "Dialogue: 0,0:03:28.06,0:03:34.06,Sign_Basic,Sign,0,0,0,,{\\fad(500,550)\\p1\\bord0\\xshad1\\yshad2\\c&H000024&\\4c&H25254C&\\pos(65,268)}m 0 0 l 50 0 l 45 13 l -5 13"
        val dez = reader.deserialize(text)
        val transformed = reader.transform(dez!!)
        assertNotNull(transformed)
    }

    @Test
    fun testSyntheticAssRead() {
        val reader = AssReader();
        val lines = SubtitleFixtures.ass
        val deserialized = lines.mapNotNull { reader.deserialize(it) }
        val mapped = deserialized.map { reader.transform(it) }

        assertEquals(deserialized.first().start, mapped.first().start.toAss())
    }


    @Test
    fun testAssFileParsing() {
        val result = SubtitleFixtures.ass.mapNotNull { reader.deserialize(it) }.map { reader.transform(it) }
        val filtered = result.filter { !it.ignore && it.type !in listOf(DialogType.SIGN_SONG, DialogType.CAPTION) }
       // val syncOrNotSync = syncDialogs(filtered)

        assertThat(result).isNotEmpty
        assertThat(filtered.map { it.text }.joinToString("\n"))
            .contains("A fictional greeting, traveler.")
    }

    @Test
    fun testSyntheticSrt() {
        val srtReader = SrtReader(null)
        val reader = srtReader.group(SubtitleFixtures.srt)
            .mapNotNull { srtReader.deserialize(it) }
            .map { srtReader.transform(it) }
        assertThat(reader).hasSize(3)
        assertEquals("Another invented line,\r\nsplit across two rows.", reader[1].text)
    }

    @Test
    fun inlineSubtitleFixturesRunThroughBothParsers() {
        val assDialogs = SubtitleFixtures.ass.mapNotNull { reader.deserialize(it) }.map { reader.transform(it) }
        val srtReader = SrtReader(null)
        val srtDialogs = srtReader.group(SubtitleFixtures.srt)
            .mapNotNull { srtReader.deserialize(it) }
            .map { srtReader.transform(it) }

        assertThat(assDialogs).isNotEmpty
        assertThat(srtDialogs).hasSize(3)
    }

}
