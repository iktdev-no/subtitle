package no.iktdev.library.subtitle.reader

import no.iktdev.library.subtitle.SubtitleFixtures
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertDoesNotThrow

class SrtReaderTest {
    private val reader = SrtReader(null)

    @Test
    fun groupTest() {
        val grouped: MutableList<List<String>> = mutableListOf()
        val cache = mutableListOf<String>()

        val lines = SubtitleFixtures.srt
        lines.forEach {
            if (it.isNotEmpty() && it.isNotBlank())
                cache.add(it)

            if (it.isEmpty() || it.isBlank() || lines.last() == it) {
                grouped.add(cache.toList())
                cache.clear()
            }
        }


        assertTrue(grouped.isNotEmpty())
    }

    @Test
    fun testSyntheticSrt_Read() {
        val lines = SubtitleFixtures.srt
        val srts = reader.group(lines).mapNotNull { reader.deserialize(it) }
        val mapped =  srts.map { reader.transform(it) }
        assertEquals(3, mapped.size)
    }


}
