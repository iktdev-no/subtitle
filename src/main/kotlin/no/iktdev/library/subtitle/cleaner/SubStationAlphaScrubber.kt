package no.iktdev.library.subtitle.cleaner

class SubStationAlphaScrubber {
    companion object {
        // ASS override blocks contain styling/drawing commands. Removing the entire
        // block leaves the visible subtitle text intact, including inline overrides.
        private val overrideBlock = Regex("\\{[^}]*}")

        fun stripTagging(text: String): String {
            return overrideBlock.replace(text, "")
        }

    }
}
