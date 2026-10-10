open class Resources {
    open val path: List<String> = listOf("")

    fun getText(path: List<String>): String? {
        val fullPath = path.joinToString("/")
        return this.javaClass.classLoader.getResource(fullPath)?.readText()
    }

    fun getLines(path: List<String>): List<String> {
        val fullPath = path.joinToString("/")
        return this.javaClass.getResourceAsStream(fullPath)?.bufferedReader()?.readLines() ?: emptyList()
    }

    open class Output: Resources() {
        override val path: List<String> = listOf("output")
        open class Encoder(): Output() {
            override val path: List<String> = listOf("output", "encoder")

            fun sample1(): List<String> {
                val text = getText(listOf(*path.toTypedArray(), "sample1.txt")) ?: return emptyList()
                return text.split(System.lineSeparator(), ignoreCase = true)
            }

            fun sample2(): List<String> {
                val text =  getText(listOf(*path.toTypedArray(), "sample2.txt")) ?: return emptyList()
                return text.split(System.lineSeparator(), ignoreCase = true)

            }

            fun sample3(): List<String> {
                val text =  getText(listOf(*path.toTypedArray(), "sample3.txt")) ?: return emptyList()
                return text.split(System.lineSeparator(), ignoreCase = true)

            }

            fun sample4(): List<String> {
                val text =  getText(listOf(*path.toTypedArray(), "sample4.txt")) ?: return emptyList()
                return text.split(System.lineSeparator(), ignoreCase = true)

            }
        }

        open class Streams: Output() {
            override val path: List<String> = listOf("output", "streams")

            fun getSample(number: Int): String {
                return getText(listOf(*path.toTypedArray(), "sample$number.json"))!!
            }
        }

    }



}
