package net.sharplab.translator.app.cli

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import net.sharplab.translator.app.service.DocL10NKitAppService
import net.sharplab.translator.app.setting.DocL10NKitSetting
import picocli.CommandLine
import picocli.CommandLine.IFactory
import java.io.File
import javax.inject.Inject

@QuarkusMain
@CommandLine.Command(name = "quarkus-adoc-po-translator", subcommands = [DocL10NKitCli.TranslateCommand::class, DocL10NKitCli.GlossaryCommand::class])
class DocL10NKitCli : QuarkusApplication {

    @Inject
    var factory: IFactory? = null

    @CommandLine.Command(name = "translate")
    class TranslateCommand(private val asciiDocPoTranslatorAppService: DocL10NKitAppService, private val asciiDocPoTranslatorSetting: DocL10NKitSetting) : Runnable {

        @CommandLine.Parameters(description = ["file path"])
        private var path: List<File>? = null
        @CommandLine.Option(order = 2, names = ["--srcLang"], description = ["source language"])
        private var srcLang: String? = null
        @CommandLine.Option(order = 3, names = ["--dstLang"], description = ["destination language"])
        private var dstLang: String? = null
        @CommandLine.Option(order = 4, names = ["--asciidoctor"], description = ["asciidoctor"])
        private var asciidoctor = true
        @CommandLine.Option(order = 5, names = ["--glossaryId"], description = ["glossary id"])
        private var glossaryId: String? = null
        @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
        private var help = false

        override fun run() {
            val filePath = path?: throw IllegalArgumentException("path must be provided")
            val resolvedSrcLang = srcLang ?: asciiDocPoTranslatorSetting.defaultSrcLang ?: throw IllegalArgumentException("srcLang must be provided")
            val resolvedDstLang = dstLang ?: asciiDocPoTranslatorSetting.defaultDstLang ?: throw IllegalArgumentException("dstLang must be provided")
            filePath.forEach {
                asciiDocPoTranslatorAppService.translateAsciiDocPoFile(it, resolvedSrcLang, resolvedDstLang, asciidoctor, glossaryId)
            }
        }

    }

    @CommandLine.Command(name = "glossary", subcommands = [GlossaryCommand.CreateCommand::class, GlossaryCommand.ListCommand::class, GlossaryCommand.DeleteCommand::class])
    class GlossaryCommand(){

        @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
        private var help = false

        @CommandLine.Command(name = "create")
        class CreateCommand(private val asciiDocPoTranslatorAppService: DocL10NKitAppService, private val asciiDocPoTranslatorSetting: DocL10NKitSetting, private val objectMapper: ObjectMapper): Runnable{
            @CommandLine.Parameters(description = ["file path"])
            private var path: File? = null
            @CommandLine.Option(order = 1, names = ["--name"], description = ["glossary name"], required = true)
            private var name: String? = null
            @CommandLine.Option(order = 2, names = ["--srcLang"], description = ["source language"])
            private var srcLang: String? = null
            @CommandLine.Option(order = 3, names = ["--dstLang"], description = ["destination language"])
            private var dstLang: String? = null

            @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
            private var help = false

            override fun run() {
                val filePath = path?: throw IllegalArgumentException("path must be provided")
                val resolvedSrcLang = srcLang ?: asciiDocPoTranslatorSetting.defaultSrcLang ?: throw IllegalArgumentException("srcLang must be provided")
                val resolvedDstLang = dstLang ?: asciiDocPoTranslatorSetting.defaultDstLang ?: throw IllegalArgumentException("dstLang must be provided")
                val glossary = asciiDocPoTranslatorAppService.createGlossary(name!!, resolvedSrcLang, resolvedDstLang, filePath)
                println(objectMapper.writeValueAsString(glossary))
            }
        }

        @CommandLine.Command(name = "list")
        class ListCommand(
            private val asciiDocPoTranslatorAppService: DocL10NKitAppService,
            private val objectMapper: ObjectMapper
        ): Runnable{
            override fun run() {
                val glossaries = asciiDocPoTranslatorAppService.listGlossaries()
                println(objectMapper.writeValueAsString(glossaries))
            }
        }

        @CommandLine.Command(name = "delete")
        class DeleteCommand(private val asciiDocPoTranslatorAppService: DocL10NKitAppService): Runnable{
            @CommandLine.Option(order = 1, names = ["--glossaryId"], description = ["glossary id"], required = true)
            private var glossaryId: String? = null

            @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
            private var help = false

            override fun run() {
                asciiDocPoTranslatorAppService.removeGlossary(glossaryId!!)
            }
        }

    }

    @CommandLine.Command(name = "normalize")
    class NormalizeCommand(private val asciiDocPoTranslatorAppService: DocL10NKitAppService) : Runnable {

        @CommandLine.Parameters(description = ["file path"])
        private var path: List<File>? = null
        @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
        private var help = false

        override fun run() {
            val filePath = path?: throw IllegalArgumentException("path must be provided")
            filePath.forEach {
                //asciiDocPoTranslatorAppService.translateAsciiDocPoFile(it)
            }
        }

    }

    @Throws(Exception::class)
    override fun run(vararg args: String?): Int {
        val commandLine = CommandLine(this, factory)
        commandLine.isCaseInsensitiveEnumValuesAllowed = true
        return commandLine.execute(*args)
    }

}