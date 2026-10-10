package no.iktdev.library.subtitle.classes

import java.util.*
import java.util.concurrent.TimeUnit

data class Time(val hour: Int, val minute: Int, val seconds: Int, val milliSeconds: Int) {
    companion object {
        private val cDotFormat = arrayOf(":",".")
        private val cCFormat = arrayOf(":", ",")

        private fun fromSplatter(value: List<String>): Array<String> {
            return arrayOf<String>(
                value[0],
                value[1],
                value[2],
                value[3]
            )
            /*val h = value[0].toInt()
            val m = value[1].toInt()
            val s = if (value.size > 2) value[2].toInt() else 0
            val f = if (value.size > 3) value[3].toInt() else 0*/
            //return Time(h, m, s, f)
        }

        private fun toTimeFromSplatter(timeBreak: Array<String>): Time {
            val h = timeBreak[0].toInt()
            val m = timeBreak[1].toInt()
            val s = when (timeBreak[2].length) {
                2 -> timeBreak[2].toInt()
                1 -> "0${timeBreak[2]}".toInt()
                else -> 0
            }
            val f = when(timeBreak[3].length) {
                3 -> timeBreak[3].toInt()
                2 -> "0${timeBreak[3]}".toInt()
                else -> 0
            }
            return Time(h, m, s, f)
        }

        fun fromAss(value: String): Time {
            // ASS Timespec is : h:mm:ss:xx
            val timed = fromSplatter(value.split(delimiters = cDotFormat))
            timed[3] = timed[3] + "0"
            return toTimeFromSplatter(timed)
        }
        fun fromSrt(value: String): Time {
            return toTimeFromSplatter(fromSplatter(value.split(delimiters = cCFormat)))
        }
        fun fromSmi(value: Long): Time {
            val timed = Calendar.getInstance().apply {
                timeInMillis = value
            }
            return Time(timed.get(Calendar.HOUR), timed.get(Calendar.MINUTE), timed.get(Calendar.SECOND), timed.get(Calendar.MILLISECOND))
        }

        fun fromMilliseconds(value: Long): Time {
            val timed = Calendar.getInstance().apply {
                timeInMillis = value
            }
            return Time(timed.get(Calendar.HOUR), timed.get(Calendar.MINUTE), timed.get(Calendar.SECOND), timed.get(Calendar.MILLISECOND))
        }
    }

    private fun hourString(): String {
        return if (hour < 10) "0$hour" else hour.toString()
    }
    private fun minuteString(): String {
        return if (minute < 10) "0$minute" else minute.toString()
    }
    private fun secondString(): String {
        return if (seconds < 10) "0$seconds" else seconds.toString()
    }
    private fun millisecondString(): String {
        return when (milliSeconds.toString().length) {
            1 -> "00$milliSeconds"
            2 -> "0$milliSeconds"
            3 -> milliSeconds.toString()
            else -> "000"
        }

        //return if (milliSeconds < 10) (milliSeconds.toString() + "00") else if (milliSeconds < 100) (milliSeconds.toString() + "0") else milliSeconds.toString()
    }

    fun toAss(): String {
        return "${hourString().toInt()}:${minuteString()}:${secondString()}.${millisecondString().dropLast(1)}"
    }

    fun toSrt(): String {
        return "${hourString()}:${minuteString()}:${secondString()},${millisecondString()}"
    }
    fun toVtt(): String {
        return "${hourString()}:${minuteString()}:${secondString()}.${millisecondString()}"
    }
    fun inMilli(): Long  = toMilli()
    fun toMilli(): Long {
        return TimeUnit.HOURS.toMillis(hour.toLong()) +
                TimeUnit.MINUTES.toMillis(minute.toLong()) +
                TimeUnit.SECONDS.toMillis(seconds.toLong()) +
                milliSeconds
    }
}