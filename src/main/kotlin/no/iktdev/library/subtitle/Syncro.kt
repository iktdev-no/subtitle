package no.iktdev.library.subtitle

import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.classes.DialogType
import no.iktdev.library.subtitle.classes.Time

/**
 * Used to merge and handle overlapping dialogs
 * It will aslo exclude SIGN_SONG from dialog if overlapping (if enabled)
 */
open class Syncro() {


    fun isInConflictOfRef(ref: Dialog, inq: Dialog): Boolean {
        return !isEndChange(ref, inq) && isAppendedInTimelineOfRef(ref, inq) || isTrackInRefAndEndsAfter(ref, inq)
    }

    /**
     *  Checks if the change is normal
     *
     *      [--------------------------------------]
     *                                             [------------]
     */
    fun isEndChange(ref: Dialog, check: Dialog): Boolean {
        return ref.end.inMilli() <= check.start.inMilli()
    }


    /**
     *
     *  Track 1:       [-------------------------]
     *  Tracl 2:           [---------------]
     *
     *  Track 2.A:     [-------------------------]
     *
     */
    fun isAppendedInTimelineOfRef(ref: Dialog, inq: Dialog): Boolean {
        return inq.start.inMilli() >= ref.start.inMilli() && ref.end.inMilli() >= inq.end.inMilli()
    }


    /**
     *
     *      [------------------] (Ref)
     *
     *      [------------------------------------] (A)
     *          [--------------------------------] (B)
     *
     */
    fun isTrackInRefAndEndsAfter(ref: Dialog, inq: Dialog): Boolean {
        return (inq.start.inMilli() >= ref.start.inMilli()) && inq.end.inMilli() > ref.end.inMilli()
    }


    /**
     * This function will return dialogs that are directly overlapping the previous one
     * In case a dialog is overlapping more than one dialog, this will need to be ran again
     * @return List<String> of overlapping dialogs
     */
    protected fun getOverlapping(items: List<Dialog>): List<Pair<Dialog, List<Dialog>>> {
        /* In order to ensure that we don't get any edge cases where the dialog
            that starts first comes later than an overlapping dialog
        */
        val _items = items.sortedBy { it.start.inMilli() }

        val overlapping: MutableList<Pair<Dialog, List<Dialog>>> = mutableListOf()

        _items.forEach { ref ->
            if (!overlapping.any { it.first == ref || it.second.contains(ref) }) {
                if (ref.type == DialogType.SIGN_SONG) {
                    ref.ignore = true
                } else {
                    val overlappingRef = items.filter { ref != it }.filter { isInConflictOfRef(ref, it ) }
                    overlapping.add(Pair(ref, overlappingRef))
                }
            }
        }
        return overlapping
    }

    /**
     * @param List<Dialog> list of overlapping
     * @return List<Dialog> of newly created dialogs based on overlapping
     */
    fun sync(items: List<Dialog>): List<Dialog> {
       // val returnable = items.filter { c -> overlapping.find { q -> q.first == c || q.second.contains(c) } != null }.toMutableList() // This list should now not contain any item that is overlapping
        val overlapping = getOverlapping(items)

        val returnable = tracks(items )

        return (returnable).sortedBy { it.start.inMilli() }
    }

    @Deprecated("Not in use")
    protected fun hasOverlapping(items: List<Dialog>): Boolean {
        items.forEach { c ->
            val isConflict =  items.any { isInConflictOfRef(c, it) }
            if (isConflict)
                return true
        }
        return false
    }


    protected fun trackSegments(items: List<Dialog>): List<Dialog> {
        val segments = mutableListOf<Dialog>()
        for (c: Dialog in items) {
            val followingDialog = items.after(c).minByOrNull { it.start.inMilli() } ?: continue
            if (followingDialog.start.inMilli() < c.end.inMilli() && c.end.inMilli() > followingDialog.end.inMilli()) {

                segments.add(Dialog(c.id, followingDialog.start, followingDialog.end, c.text, c.type))
                segments.add(Dialog(c.id, followingDialog.end, c.end, c.text, c.type))
                c.end = followingDialog.start // Sets the current end to the start of the overlapping

            }
            else if  (followingDialog.start.inMilli() < c.end.inMilli()) {
                segments.add(Dialog(c.id, followingDialog.start, c.end, c.text, c.type))
                c.end = followingDialog.start
            } else {
                // SKIP
                c.end
            }

        }
        return segments
    }

    protected fun tracks(items: List<Dialog>): List<Dialog> {
        var dialogs = items.sortedBy { it.start.inMilli() }.toMutableList()
        val segments = trackSegments(dialogs)
        dialogs.addAll(segments)
        dialogs = dialogs.sortedBy { it.start.inMilli() }.toMutableList()

        val copy = dialogs.toList()
        for (c in copy) {
            if (!dialogs.contains(c))
                continue

            val conflict = dialogs.filter { isAppendedInTimelineOfRef(c, it) && it != c }
            conflict.forEach { dialogs.remove(it) }
            if (conflict.isNotEmpty()) {
                dialogs.remove(c)
                val lines = conflict.map { it.text }.distinctBy { it }
                dialogs.add(newTrackInTimeline(listOf(c.text) + lines, c.start, c.end))
            }
        }

        return dialogs.sortedBy { it.start.inMilli() }
    }



    protected fun newTrackInTimeline(texts: List<String>, start: Time, end: Time): Dialog {
        return Dialog(
            start = start,
            end = end,
            text = texts.joinToString(Configuration.carriageReturn),
            type = DialogType.DIALOG_NORMAL
        )
    }

}

fun <T> List<T>.after(item: T): List<T> {
    if (!this.contains(item))
        throw RuntimeException("Item does not exist in list!")
    return if (this.indexOf(item) == this.lastIndex)
        emptyList<T>()
    else
        this.subList(this.indexOf(item)+1, this.size)
}