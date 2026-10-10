package no.iktdev.library.subtitle

import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.classes.DialogType
import no.iktdev.library.subtitle.classes.Time
import no.iktdev.library.subtitle.reader.AssReader
import org.junit.Assert.*
import org.junit.Test

class SyncroTest: Syncro() {

    private val baseSet1 = listOf<Dialog>(
        Dialog(
            start = Time(0, 0,0,0),
            end = Time(0,0,10,0),
            text = "Start item",
            type = DialogType.DIALOG_NORMAL
        ),
        Dialog(
            id = "target",
            start = Time(0, 0,10,0),
            end = Time(0,0,20,0),
            text = "Item #1A",
            type = DialogType.DIALOG_NORMAL
        ),
        Dialog(
            start = Time(0, 0,15,0),
            end = Time(0,0,20,0),
            text = "Item #1B",
            type = DialogType.DIALOG_NORMAL
        ),
        Dialog(
            start = Time(0, 0,25,0),
            end = Time(0,0,30,0),
            text = "Item #2",
            type = DialogType.DIALOG_NORMAL
        ),
        Dialog(
            start = Time(0, 0,30,0),
            end = Time(0,0,40,0),
            text = "Item 3",
            type = DialogType.DIALOG_NORMAL
        )
    )
    get() = field.toList()

    private val baseSet2 = listOf<Dialog>(
        Dialog(
            start = Time(0, 0,0,0),
            end = Time(0,0,10,0),
            text = "Start item",
            type = DialogType.DIALOG_NORMAL
        ),
        Dialog(
            id = "target",
            start = Time(0, 0,10,0),
            end = Time(0,0,25,0),
            text = "Item #1A",
            type = DialogType.DIALOG_NORMAL
        ),
        Dialog(
            start = Time(0, 0,15,0),
            end = Time(0,0,20,0),
            text = "Item #1B",
            type = DialogType.DIALOG_NORMAL
        ),
        Dialog(
            start = Time(0, 0,25,0),
            end = Time(0,0,30,0),
            text = "Item #2",
            type = DialogType.DIALOG_NORMAL
        ),
        Dialog(
            start = Time(0, 0,30,0),
            end = Time(0,0,40,0),
            text = "Item 3",
            type = DialogType.DIALOG_NORMAL
        )
    )
        get() = field.toList()


    @Test
    fun testTrackSegmentation() {
        val mockMergable = baseSet1

        val segmentation = trackSegments(mockMergable)
        assertTrue("List is empty", segmentation.isNotEmpty())
        assertEquals(1, segmentation.size)
        assertTrue(mockMergable.find { it.id == "target" }?.end?.seconds == 15)
    }

    @Test
    fun testTrackSegmentation2() {
        val mockMergable = baseSet2

        val segmentation = trackSegments(mockMergable)
        assertTrue("List is empty", segmentation.isNotEmpty())
        assertEquals(2, segmentation.size)
        assertTrue(mockMergable.find { it.id == "target" }?.end?.seconds == 15)
    }

    @Test
    fun testTrackMerging() {
        val mockMergable = baseSet2

        val merged = tracks(mockMergable)
        assertTrue("List is empty", merged.isNotEmpty())
        assertEquals(6, merged.size)
    }


    @Test
    fun testSync() {
        val merged = sync(baseSet2)
        assertTrue("List is empty", merged.isNotEmpty())
        assertEquals(6, merged.size)
    }

    @Test
    fun testAphocracy() {
        val reader = AssReader();
        val lines = SubtitleFixtures.ass
        val deserialized = lines.mapNotNull { reader.deserialize(it) }
        val mapped = deserialized.map { reader.transform(it) }

        val merged = sync(mapped)
        assertTrue(merged.size == mapped.size)
    }

    @Test
    fun testSyncroNonDuplicates() {

    }



}
