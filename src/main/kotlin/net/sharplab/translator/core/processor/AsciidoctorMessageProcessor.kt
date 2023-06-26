package net.sharplab.translator.core.processor

import org.asciidoctor.Asciidoctor
import org.asciidoctor.Options
import org.jsoup.Jsoup
import java.nio.file.Files

class AsciidoctorMessageProcessor {

    private val asciidoctor = Asciidoctor.Factory.create()
    private val tempDir = Files.createTempDirectory("asciidoc-templates-")

    init {
        val inputStream = this.javaClass.classLoader.getResourceAsStream("asciidoc-templates/inline_anchor.html.erb")
        val inlineAnchorTemplateFile = tempDir.toFile().resolve("inline_anchor.html.erb")
        Files.copy(inputStream!!, inlineAnchorTemplateFile.toPath())
    }

    fun preProcess(message: String): String {
        val options = Options.builder()
            .templateDirs(tempDir.toFile())
            .build()
        val document = asciidoctor.load(message, options)
        document.attributes["relfilesuffix"] = ".adoc"
        val html = document.convert()
        val doc = Jsoup.parseBodyFragment(html)
        return when (val first = doc.body().children().first()) {
            null -> message
            else -> first.children().html()
        }
    }

    fun postProcess(message: String): String {
        val doc = Jsoup.parseBodyFragment(message)
        val body = doc.body()
        LinkTagPostProcessor().postProcess(body)
        ImageTagPostProcessor().postProcess(body)
        DecorationTagPostProcessor("em", "_", "_").postProcess(body)
        DecorationTagPostProcessor("strong", "*", "*").postProcess(body)
        DecorationTagPostProcessor("monospace", "`", "`").postProcess(body)
        DecorationTagPostProcessor("superscript", "^", "^").postProcess(body)
        DecorationTagPostProcessor("subscript", "~", "~").postProcess(body)
        DecorationTagPostProcessor("code", "`", "`").postProcess(body)
        val wholeText = body.wholeText()
        return unescapeCharacterReference(wholeText)
    }

    private fun unescapeCharacterReference(str: String): String{
        return str
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
    }

}