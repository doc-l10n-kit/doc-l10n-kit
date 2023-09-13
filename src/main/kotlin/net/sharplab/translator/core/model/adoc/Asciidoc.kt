package net.sharplab.translator.core.model.adoc

import net.sharplab.translator.core.driver.adoc.AsciidocParser
import net.sharplab.translator.core.driver.adoc.ext.StringExt.Companion.calculateLines
import net.sharplab.translator.core.driver.adoc.ext.StringExt.Companion.indexOfBeginningOfLine
import net.sharplab.translator.core.driver.adoc.ext.StringExt.Companion.indexOfEndOfLine
import net.sharplab.translator.core.model.po.Po
import net.sharplab.translator.core.model.po.PoMessageIndex
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class Asciidoc internal constructor(val path: Path, value: String, sentences: List<AsciidocParser.SentenceCandidate>) {

    private val logger = LoggerFactory.getLogger(Asciidoc::class.java)
    var value : String = value
        private set
    val sentences: List<Sentence>

    init {
        this.sentences = sentences.map { Sentence(it.type, it.text, it.claimedLineNumber) }
    }


    fun translate(po: Po){
        val messageIndex = PoMessageIndex(po)
        if(messageIndex.isEmpty()){
            return
        }
        sentences
                .filterNot { it.type == SentenceType.SOURCE }
                .forEach{ sentence ->
            val translated = when(val poMessage = messageIndex[sentence.text]){
                null -> return@forEach
                else -> when(poMessage.messageString){
                    "" -> return@forEach
                    else -> {
                        when(poMessage.fuzzy){
                            true -> return@forEach
                            false -> poMessage.messageString
                        }
                    }
                }
            }
            sentence.replaceWith(translated)
        }
    }

    private fun publishLineChange(lineNumber: Int, amount: Int){
        sentences.filter { it.lineNumber >= lineNumber }.forEach{ it.lineNumber += amount }
    }

    inner class Sentence(val type: SentenceType, text: String, lineNumber: Int) {

        private val logger = LoggerFactory.getLogger(Sentence::class.java)

        var text: String = text
            private set

        var lineNumber: Int = lineNumber
            internal set

        val lines: Int
            get() = text.calculateLines()

        private fun createTextRangeCandidate() : TextRange{
            val nextSentenceLineNumber = nextSentence?.lineNumber ?: value.calculateLines()
            val fromIndex = value.indexOfBeginningOfLine(lineNumber)
            val toIndex = value.indexOfEndOfLine(nextSentenceLineNumber) //over-wrap to the first line of next sentence intentionally because the end of table cell may be over-wrapped with next cell.
            return createTextRangeFromIndex(fromIndex, toIndex)
        }

        private fun createTextRange(): TextRange{
            val textRangeCandidate = createTextRangeCandidate()
            if(textRangeCandidate.find(text)){
                return textRangeCandidate
            }
            else{
                val fromIndex = value.indexOfBeginningOfLine(lineNumber)
                val toIndex = value.length //EoF
                val textRange = createTextRangeFromIndex(fromIndex, toIndex)
                if (textRange.find(text)) {
                    return textRange
                }
                logger.error("source file: ${this@Asciidoc.path}")
                logger.error("source text: ${textRange.string}")
                logger.error("Failed to find `${text}` from source text.")
                throw IllegalStateException("Failed to find `${text}` from source text.")
            }
        }

        val nextSentence: Sentence? by lazy {
            val iterator = sentences.iterator()
            while(iterator.hasNext()){
                val sentence = iterator.next()
                if(sentence == this){
                    return@lazy when {
                        iterator.hasNext() -> iterator.next()
                        else -> null
                    }
                }
            }
            null
        }

        fun replaceWith(newText: String){
            val textRange = createTextRange()
            if (textRange.find(text)) {
                val previousString = textRange.string
                textRange.replace(text, newText)
                val afterString = textRange.string
                val amount = afterString.calculateLines() - previousString.calculateLines()
                text = newText
                publishLineChange(lineNumber, amount)
            } else {
                logger.warn("'${text}' is not found in the source text")
                logger.warn("source text: ${textRange.string}")
                logger.warn("source file: ${path.absolutePathString()}")
                return
            }
        }

        private fun createTextRangeFromIndex(fromIndex: Int, toIndex: Int): TextRange{
            if(fromIndex < 0){
                throw IllegalArgumentException("fromIndex must not be less than 0")
            }
            if(toIndex < 0){
                throw IllegalArgumentException("toIndex must not be less than 0")
            }
            if(fromIndex > toIndex){
                throw IllegalArgumentException("fromIndex must not be more than toIndex")
            }
            if(fromIndex > value.length){
                throw IllegalArgumentException("fromIndex must not be more than value.length")
            }
            if(toIndex > value.length){
                throw IllegalArgumentException("toIndex must not be more than value.length")
            }
            return TextRange(fromIndex, toIndex)
        }
    }

    inner class TextRange(private val fromIndex: Int, private var toIndex: Int) {

        val string: String
            get() = value.substring(fromIndex, toIndex)

        fun find(text: String): Boolean{
            if(toIndex == -1){
                return false
            }
            val trimmedString = createTrimmedString()
            val trimmedText = text.lines().joinToString("\n") { it.trimIndent().trimEnd() }
            if(trimmedString.indexOf(trimmedText) != -1){
                return true
            }
            if (trimmedString.indexOf(trimmedText.replace("\'","\'\'")) != -1){
                return true
            }
            return false
        }

        fun replace(oldText: String, newText: String){
            val trimmedString = createTrimmedString()
            val trimmedOldText = oldText.lines().joinToString("\n") { it.trimIndent().trimEnd() }
            var indexInTrimmedString =  trimmedString.indexOf(trimmedOldText)
            if(indexInTrimmedString == -1){
               indexInTrimmedString = trimmedString.indexOf(trimmedOldText.replace("\'","\'\'"))
            }
            if(indexInTrimmedString == -1){
                throw IllegalArgumentException("oldText is not found ind the source string")
            }
            else{
                val replacement = trimmedString.replaceRange(indexInTrimmedString, indexInTrimmedString+trimmedOldText.length, newText)
                value = value.replaceRange(fromIndex, toIndex, replacement)
                val adjustment = replacement.length - (toIndex - fromIndex)
                toIndex += adjustment
            }
        }

        private fun createTrimmedString() : String{
            return value.substring(fromIndex, toIndex).lines()
                    .filterNot { it.trimStart().startsWith("//") }
                    .joinToString("\n") { it.trimIndent().trimEnd() }
        }
    }

}