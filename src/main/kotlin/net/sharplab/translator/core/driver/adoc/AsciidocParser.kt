package net.sharplab.translator.core.driver.adoc

import net.sharplab.translator.core.driver.adoc.ext.StringExt.Companion.indexOfBeginningOfLine
import net.sharplab.translator.core.driver.adoc.ext.StringExt.Companion.normalizeLineBreak
import net.sharplab.translator.core.model.adoc.Asciidoc
import net.sharplab.translator.core.model.adoc.SentenceType
import org.asciidoctor.ast.*
import org.asciidoctor.jruby.internal.RubyObjectWrapper
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.MappingNode
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.ScalarNode
import org.yaml.snakeyaml.nodes.SequenceNode
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.readText

class AsciidocParser(private val path: Path) {

    companion object{
        fun parse(path: Path): Asciidoc{
            return AsciidocParser(path).parse()
        }
    }

    private val logger = LoggerFactory.getLogger(AsciidocParser::class.java)
    private val originalText = path.readText(StandardCharsets.UTF_8).normalizeLineBreak()
    private val preprocessorDirectiveTrimmedText : String = originalText.lines().joinToString("\n") {
        when {
            it.startsWith("ifdef::") || it.startsWith("ifndef::") || it.startsWith("ifeval::") || it.startsWith("endif::") -> ""
            else -> it
        }
    }
    private val document = AsciidocUtil.loadAsciidocString(preprocessorDirectiveTrimmedText, path.absolutePathString()) //TODO: relativize

    private fun parse(): Asciidoc {
        val sentenceCandidates: MutableList<SentenceCandidate> = mutableListOf()
        sentenceCandidates.addAll(extractSentencesFromFrontMatter())
        sentenceCandidates.addAll(extractSentences(path, preprocessorDirectiveTrimmedText, document))
        val sentences = sentenceCandidates.sortedBy { it.claimedLineNumber }
        return Asciidoc(path, originalText, sentences)
    }

    private fun extractSentencesFromFrontMatter() : List<SentenceCandidate>{
        val frontMatter = document.attributes["front-matter"]
        if(frontMatter == null || frontMatter !is String){
            return emptyList()
        }
        val trimmedAsciidoc = preprocessorDirectiveTrimmedText.lines().joinToString("\n") { it.trimEnd() }
        val trimmedFrontMatter = frontMatter.lines().joinToString("\n") { it.trimEnd() }
        val lineOffset = trimmedAsciidoc.substring(0, trimmedAsciidoc.indexOf(trimmedFrontMatter)).lines().size

        val node = Yaml().compose(StringReader(frontMatter))
        val sentences = mutableListOf<SentenceCandidate>()
        fun walkNode(node: Node){
            when(node){
                is MappingNode -> {
                    node.value.forEach{
                        walkNode(it.valueNode)
                    }
                }
                is SequenceNode -> {
                    node.value.forEach{
                        walkNode(it)
                    }
                }
                is ScalarNode -> {
                    val line = lineOffset + node.startMark.line
                    sentences.add(SentenceCandidate(SentenceType("YAML Front Matter"), node.value, line))
                }
                else -> TODO()
            }
        }
        walkNode(node)
        return sentences
    }

    private fun extractSentences(asciidocFile: Path, asciidocString: String, node: StructuralNode) : List<SentenceCandidate>{

        val sentences = mutableListOf<SentenceCandidate>()

        fun walkBlocks(asciidocFile: Path, asciidocString: String, node: StructuralNode){
            // process block titles
            val title = (node.attributes["title"] as? String).let{
                when(it){
                    null -> {
                        val titlePropertyValue = (node as RubyObjectWrapper).getRubyProperty("@title")
                        when {
                            titlePropertyValue.isNil -> node.title
                            else -> titlePropertyValue.asJavaString()
                        }
                    }
                    else -> it
                }
            }
            if(!title.isNullOrEmpty()){
                val type = calculateTitleType(node)
                when (node) {
                    is Document -> {
                        val nextBlockLineNumber = node.blocks.firstOrNull()?.sourceLocation?.lineNumber
                        val asciidocLines = asciidocString.lines()

                        val headerLines = when (nextBlockLineNumber) {
                            null -> asciidocLines
                            else -> asciidocLines.subList(0, nextBlockLineNumber)
                        }

                        headerLines.forEachIndexed { index, line ->
                            if(line.startsWith("= ") && line.substring(2).trimStart().startsWith(title)){
                                sentences.add(SentenceCandidate(type, title, index+1))
                                return@forEachIndexed
                            }
                        }
                    }
                    is Section -> {
                        sentences.add(SentenceCandidate(type, title, node.sourceLocation.lineNumber))
                    }
                    else -> {
                        val previousBlockLastLine = sentences.lastOrNull()?.claimedLineNumber ?: 0
                        var currentLineNumber = node.sourceLocation.lineNumber - 1
                        while(true) {
                            val fromIndex = asciidocString.indexOfBeginningOfLine(currentLineNumber)
                            if(asciidocString[fromIndex] == '.'){ // check if it is title line
                                val titleIndex = AsciidocUtil.indexOf(asciidocString, title, currentLineNumber, 1)
                                if(titleIndex != -1){
                                    sentences.add(SentenceCandidate(type, title, currentLineNumber))
                                    break
                                }
                            }
                            currentLineNumber--
                            if(currentLineNumber <= previousBlockLastLine){
                                //try to find in source location (assume inline attribute)
                                val titleIndex = AsciidocUtil.indexOf(asciidocString, title, node.sourceLocation.lineNumber, 1)
                                if(titleIndex != -1){
                                    sentences.add(SentenceCandidate(type, title, node.sourceLocation.lineNumber))
                                    break
                                }
                                else{
                                    throw IllegalStateException("Failed to find title '${title}' from the source test.")
                                }
                            }
                        }
                    }
                }
            }

            // process non-container blocks
            when (node) {
                is Document -> {
                    //nop //TODO: revisit
                }
                is Block -> {
                    if(!node.source.isNullOrEmpty()){
                        if(node.style == "source"){
                            sentences.add(SentenceCandidate(SentenceType.SOURCE, node.source, node.sourceLocation.lineNumber))
                        }
                        else{
                            fun findContainer(node: ContentNode): StructuralNode {
                                return when (node) {
                                    is StructuralNode -> when (node.sourceLocation.file) {
                                        null -> {
                                            if(node.parent == null){
                                                TODO()
                                            }
                                            findContainer(node.parent)
                                        }
                                        else -> node
                                    }
                                    else -> findContainer(node)
                                }
                            }
                            val lineNumber = when(node.sourceLocation.file){
                                null -> findContainer(node).sourceLocation.lineNumber + node.sourceLocation.lineNumber - 1
                                else -> node.sourceLocation.lineNumber
                            }
                            sentences.add(SentenceCandidate(SentenceType.PLAIN_TEXT, node.source, lineNumber))
                        }
                    }
                }
                is Section -> {
                    //nop //TODO: revisit
                }
                is Table -> {
                    node.header.forEach { row ->
                        row.cells.forEach {cell ->
                            walkBlocks(asciidocFile, asciidocString, cell)
                        }
                    }
                    node.body.forEach {row ->
                        row.cells.forEach {cell ->
                            walkBlocks(asciidocFile, asciidocString, cell)
                        }
                    }
                    node.footer.forEach { row ->
                        row.cells.forEach {cell ->
                            walkBlocks(asciidocFile, asciidocString, cell)
                        }
                    }
                }
                is Cell -> {
                    if(!node.source.isNullOrEmpty()){
                        sentences.add(SentenceCandidate(SentenceType.TABLE, node.source, node.sourceLocation.lineNumber))
                    }
                }
                is org.asciidoctor.ast.List -> {
                    //nop //TODO: revisit
                }
                is ListItem -> {
                    if(!node.source.isNullOrEmpty()){
                        sentences.add(SentenceCandidate(SentenceType.PLAIN_TEXT, node.source, node.sourceLocation.lineNumber))
                    }
                }
                is DescriptionList -> {
                    node.items.forEach { item ->
                        item.terms.forEach{ term ->
                            walkBlocks(asciidocFile, asciidocString, term)
                        }
                        walkBlocks(asciidocFile, asciidocString, item.description)
                    }
                }
                else -> logger.warn(node.nodeName)
            }
            node.blocks.forEach {
                walkBlocks(asciidocFile, asciidocString, it)
            }
        }
        walkBlocks(asciidocFile, asciidocString, node)
        return sentences
    }

    private fun calculateTitleType(node: StructuralNode): SentenceType {
        return when(node.level) {
            0 -> SentenceType.TITLE_1
            1 -> SentenceType.TITLE_2
            2 -> SentenceType.TITLE_3
            3 -> SentenceType.TITLE_4
            4 -> SentenceType.TITLE_5
            5 -> SentenceType.TITLE_6
            else -> TODO()
        }
    }


    class SentenceCandidate(val type: SentenceType, val text: String, val claimedLineNumber: Int)
}