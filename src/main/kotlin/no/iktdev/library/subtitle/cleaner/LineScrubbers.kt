package no.iktdev.library.subtitle.cleaner

/**
 * Removes noisy characters from the given text. Noisy characters are sequences of the same non-whitespace character
 * that occur more than four times in a row.
 *
 * @param text the text to scrub
 * @return the scrubbed text
 *
 * @example
 * // Returns "Hello world! This is a test."
 * scrubNoisyCharacters("Hheeeeelllooooo     wworllld!    This isssss aaaaa teessst.")
 */
fun scrubNoisyCharacters(text: String): String {
    return text.replace(Regex("(?<!\\S)(\\S)(?:\\s*\\1){4,}(?!\\S)"), "")
}

/**
 * Removes any sequence in the given text where a character with a backslash precedes a letter
 * and this sequence is repeated more than twice consecutively.
 *
 * @param text the text to scrub
 * @return the scrubbed text
 *
 * Example usage:
 *   scrubSlashers("\\\\\\\\t\\t\\t\\n\\n\\n") -> ""
 *   scrubSlashers("some\t\t\ttext with spaces\t\t\tand\t\t\ttabs") -> "some text with spaces and tabs"
 */
fun scrubSlashers(text: String): String {
    val pattern = "(\\\\[A-Za-z])(?:\\1*)\\1+".toRegex()
    val result =  text.replace(pattern, " ")
    return result.trim().replace(Regex("\\h|\\s+"), " ")
}
