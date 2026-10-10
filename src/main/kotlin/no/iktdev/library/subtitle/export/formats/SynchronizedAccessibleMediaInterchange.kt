package no.iktdev.library.subtitle.export.formats

import no.iktdev.library.subtitle.Configuration
import no.iktdev.library.subtitle.classes.Dialog
import no.iktdev.library.subtitle.classes.Time
import no.iktdev.library.subtitle.export.Exporter
import java.io.File

class SynchronizedAccessibleMediaInterchange(folder: File, baseName: String): Exporter(folder, baseName) {


    override fun format(dialogs: List<Dialog>): List<String> {
        val formatted: MutableList<String> = mutableListOf()
        formatted.add("<sami>")
        formatted.add("\t<head>")
        formatted.add("\t\t<title>${baseName}</title>")
        formatted.add("\t</head>")
        formatted.add("\t<body>")
        dialogs.forEach { dialog ->
            getStartItem(dialog).forEach { line ->
                formatted.add(line)
            }
            getEndItem(dialog.end).forEach { line ->
                formatted.add(line)
            }
        }

        formatted.add("\t</body>")
        formatted.add("</sami>")
        return formatted
    }

    private fun getStartItem(dialog: Dialog): List<String> {
        return listOf(
            "\t\t<sync Start=\"${dialog.start.inMilli()}\">",
            "\t\t\t<p>${dialog.text}</p>",
            "\t\t</sync>",
        )
    }

    private fun getEndItem(time: Time): List<String> {
        return listOf(
            "\t\t<sync Start=\"${time.inMilli()}\">",
            "\t\t\t<p>&amp;nbsp;</p>",
            "\t\t</sync>",
        )
    }

    override fun write(dialogs: List<Dialog>): File {
        val file = outFile("smi")
        deleteOnExistAndOverwriteAllowed(file)
        val formatted = format(dialogs)
        file.printWriter().use { out ->
            formatted.forEach {
                out.println(it)
            }
        }
        return file
    }


}