package no.iktdev.library.subtitle

/** Small synthetic inputs that preserve parser edge cases without shipping subtitle files. */
object SubtitleFixtures {
    val ass: List<String> = """
        [Script Info]
        Title: Synthetic parser fixture
        ScriptType: v4.00+

        [Events]
        Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
        Dialogue: 0,0:00:01.00,0:00:03.00,Default,Traveler,0,0,0,,{\an8\i1}A fictional greeting, traveler.{\i0}\NWelcome.
        Dialogue: 0,0:00:03.20,0:00:05.00,Default,Guide,0,0,0,,{\b1}This invented line is bold.{\b0}
        Dialogue: 0,0:00:05.10,0:00:06.50,Sign_Scene,Sign,0,0,0,,A made-up sign
    """.trimIndent().lines()

    val srt: List<String> = """
        1
        00:00:01,000 --> 00:00:02,500
        A fictional greeting.

        2
        00:00:03,000 --> 00:00:04,500
        Another invented line,
        split across two rows.

        3
        00:00:05,000 --> 00:00:06,000
        The imaginary scene ends.
    """.trimIndent().lines()
}
