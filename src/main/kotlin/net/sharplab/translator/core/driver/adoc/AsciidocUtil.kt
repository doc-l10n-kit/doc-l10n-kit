package net.sharplab.translator.core.driver.adoc

import net.sharplab.translator.core.driver.adoc.ext.StringExt.Companion.indexOfBeginningOfLine
import net.sharplab.translator.core.driver.adoc.ext.StringExt.Companion.indexOfEndOfLine
import org.asciidoctor.Asciidoctor
import org.asciidoctor.Attributes
import org.asciidoctor.Options
import org.asciidoctor.ast.Document
import org.asciidoctor.log.Severity
import org.slf4j.LoggerFactory
import java.nio.file.Path

class AsciidocUtil {

    companion object{

        private val logger = LoggerFactory.getLogger(AsciidocUtil::class.java)

        private val asciidoctor = Asciidoctor.Factory.create()

        init{
            asciidoctor.registerLogHandler { logRecord ->
                @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                when (logRecord.severity) {
                    Severity.DEBUG -> logger.debug("{}: {}", logRecord.cursor, logRecord.message)
                    Severity.INFO -> logger.info("{}: {}", logRecord.cursor, logRecord.message)
                    Severity.WARN -> logger.warn("Asciidoc WARN: {}: {}", logRecord.cursor, logRecord.message)
                    Severity.ERROR -> logger.warn("Asciidoc ERROR: {}: {}", logRecord.cursor, logRecord.message)
                    Severity.FATAL -> logger.warn("Asciidoc FATAL: {}: {}", logRecord.cursor, logRecord.message)
                    Severity.UNKNOWN -> logger.warn("Asciidoc UNKNOWN: {}: {}", logRecord.cursor, logRecord.message)
                }
            }
        }

        fun loadAsciidoc(path: Path): Document {
            val attributes = Attributes.builder()
                .attribute("skip-front-matter")
                .build()

            val options = Options.builder()
                .sourcemap(true)
                .catalogAssets(true)
                .attributes(attributes)
                .build()
            return asciidoctor.loadFile(path.toFile(), options)
        }

        fun loadAsciidocString(string: String, path: String? = null): Document {
            val attributesBuilder = Attributes.builder()
                    .attribute("skip-front-matter")
            if(path != null){
                attributesBuilder.attribute("docfile", path)
            }
            val attributes = attributesBuilder.build()

            val options = Options.builder()
                    .sourcemap(true)
                    .catalogAssets(true)
                    .attributes(attributes)
                    .build()
            return asciidoctor.load(string, options)
        }

        fun indexOf(asciidocString: String, string: String, lineNumber: Int, lines: Int): Int{
            val fromIndex = asciidocString.indexOfBeginningOfLine(lineNumber)
            val toIndex = asciidocString.indexOfBeginningOfLine(lineNumber+lines)

            val index = asciidocString.indexOf(string, fromIndex)
            return when (index) {
                -1 -> -1
                else -> when {
                    index > toIndex -> -1
                    else -> index
                }
            }
        }
    }
}