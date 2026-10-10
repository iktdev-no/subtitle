package no.iktdev.library.subtitle.classes

import org.jetbrains.annotations.NotNull
import java.util.*

data class Dialog(
    val id: String = UUID.randomUUID().toString(),
    @NotNull var start: Time,
    @NotNull var end: Time,
    var text: String,
    val type: DialogType,
    var ignore: Boolean = false
)