package net.sharplab.translator.app.cli

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import net.sharplab.translator.app.exception.DocL10NKitAppException
import net.sharplab.translator.app.service.DocL10NKitAppService
import net.sharplab.translator.app.setting.DocL10NKitSetting
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.IFactory
import java.nio.file.Path
import javax.inject.Inject
import kotlin.system.exitProcess

@QuarkusMain
@CommandLine.Command(name = "doc-l10n-kit", subcommands = [DocL10NKitCli.PoCommand::class, DocL10NKitCli.GlossaryCommand::class])
class DocL10NKitCli : QuarkusApplication {

    @Inject
    lateinit var factory: IFactory

    @CommandLine.Command(name = "po", subcommands = [PoCommand.MachineTranslateCommand::class, PoCommand.ApplyTmxCommand::class, PoCommand.ApplyFuzzyTmxCommand::class, PoCommand.NormalizeCommand::class])
    class PoCommand() {
        @CommandLine.Command(name = "machine-translate")
        class MachineTranslateCommand(private val docL10nKitAppService: DocL10NKitAppService, private val docL10nKitSetting: DocL10NKitSetting) : Runnable {

            private val logger = LoggerFactory.getLogger(this::class.java)

            @CommandLine.Parameters(description = ["file path"])
            private var path: List<Path>? = null
            @CommandLine.Option(order = 2, names = ["--source"], description = ["source language"])
            private var source: String? = null
            @CommandLine.Option(order = 3, names = ["--target"], description = ["target language"])
            private var target: String? = null
            @CommandLine.Option(order = 4, names = ["--isAsciidoc"], description = ["enable or disable asciidoc inline markup processing"])
            private var asciidoc = true
            @CommandLine.Option(order = 5, names = ["--glossaryId"], description = ["glossary id"])
            private var glossaryId: String? = null
            @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
            private var help = false

            override fun run() {
                try{
                    val filePath = path?: throw IllegalArgumentException("path must be provided")
                    val resolvedSourceLang = source ?: docL10nKitSetting.defaultSourceLang
                    val resolvedTargetLang = target ?: docL10nKitSetting.defaultTargetLang

                    filePath.forEach {
                        docL10nKitAppService.machineTranslatePoFile(it, resolvedSourceLang, resolvedTargetLang, asciidoc, glossaryId)
                    }
                }
                catch (e: DocL10NKitAppException){
                    logger.error("doc-l10n-kit failed with error: ${e.message}", e)
                }
                catch (e: Exception){
                    logger.error("doc-l10n-kit failed with error: ${e.message}\n${e.stackTraceToString()}", e)
                    exitProcess(1)
                }
            }
        }

        @CommandLine.Command(name = "apply-tmx", description = ["Translate messages in .po file with tmx file"])
        class ApplyTmxCommand(private val docL10nKitAppService: DocL10NKitAppService) : Runnable {


            private val logger = LoggerFactory.getLogger(this::class.java)

            @CommandLine.Option(order = 1, names = ["--tmx"], description = ["tmx"])
            private lateinit var tmx: Path
            @CommandLine.Option(order = 2, names = ["--po"], description = ["po"])
            private lateinit var po: Path
            @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
            private var help = false

            override fun run() {
                try{
                    docL10nKitAppService.applyConfirmedTmx(tmx, po)
                }
                catch (e: DocL10NKitAppException){
                    logger.error("doc-l10n-kit failed with error: ${e.message}", e)
                }
                catch (e: Exception){
                    logger.error("doc-l10n-kit failed with error: ${e.message}\n${e.stackTraceToString()}", e)
                    exitProcess(1)
                }
            }
        }

        @CommandLine.Command(name = "apply-fuzzy-tmx", description = ["Translate fuzzy messages in .po file with fuzzy tmx file"])
        class ApplyFuzzyTmxCommand(private val docL10nKitAppService: DocL10NKitAppService, private val docL10nKitSetting: DocL10NKitSetting) : Runnable {

            private val logger = LoggerFactory.getLogger(this::class.java)

            @CommandLine.Option(order = 1, names = ["--fuzzy-tmx"], description = ["fuzzy tmx"])
            private lateinit var fuzzyTmx: Path
            @CommandLine.Option(order = 2, names = ["--po"], description = ["po"])
            private lateinit var po: Path
            @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
            private var help = false

            override fun run() {
                try{
                    docL10nKitAppService.applyFuzzyTmx(fuzzyTmx, po)
                }
                catch (e: DocL10NKitAppException){
                    logger.error("doc-l10n-kit failed with error: ${e.message}", e)
                }
                catch (e: Exception){
                    logger.error("doc-l10n-kit failed with error: ${e.message}\n${e.stackTraceToString()}", e)
                    exitProcess(1)
                }
            }
        }


        @CommandLine.Command(name = "normalize")
        class NormalizeCommand(private val docL10nKitAppService: DocL10NKitAppService) : Runnable {

            private val logger = LoggerFactory.getLogger(this::class.java)

            @CommandLine.Parameters(description = ["file path"])
            private var path: List<Path>? = null
            @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
            private var help = false

            override fun run() {
                try{
                    //TODO
                }
                catch (e: DocL10NKitAppException){
                    logger.error("doc-l10n-kit failed with error: ${e.message}", e)
                }
                catch (e: Exception){
                    logger.error("doc-l10n-kit failed with error: ${e.message}\n${e.stackTraceToString()}", e)
                    exitProcess(1)
                }
            }

        }
    }

    @CommandLine.Command(name = "glossary", subcommands = [GlossaryCommand.CreateCommand::class, GlossaryCommand.ListCommand::class, GlossaryCommand.DeleteCommand::class])
    class GlossaryCommand(){

        @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
        private var help = false

        @CommandLine.Command(name = "create")
        class CreateCommand(private val docL10nKitAppService: DocL10NKitAppService, private val docL10nKitSetting: DocL10NKitSetting, private val objectMapper: ObjectMapper): Runnable{

            private val logger = LoggerFactory.getLogger(this::class.java)

            @CommandLine.Parameters(description = ["file path"])
            private var path: Path? = null
            @CommandLine.Option(order = 1, names = ["--name"], description = ["glossary name"], required = true)
            private var name: String? = null
            @CommandLine.Option(order = 2, names = ["--source"], description = ["source language"])
            private var source: String? = null
            @CommandLine.Option(order = 3, names = ["--target"], description = ["target language"])
            private var target: String? = null

            @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
            private var help = false

            override fun run() {
                try{
                    val filePath = path?: throw IllegalArgumentException("path must be provided")
                    val resolvedSourceLang = source ?: docL10nKitSetting.defaultSourceLang
                    val resolvedTargetLang = target ?: docL10nKitSetting.defaultTargetLang
                    val glossary = docL10nKitAppService.createGlossary(name!!, resolvedSourceLang, resolvedTargetLang, filePath)
                    println(objectMapper.writeValueAsString(glossary))
                }
                catch (e: DocL10NKitAppException){
                    logger.error("doc-l10n-kit failed with error: ${e.message}", e)
                }
                catch (e: Exception){
                    logger.error("doc-l10n-kit failed with error: ${e.message}\n${e.stackTraceToString()}", e)
                    exitProcess(1)
                }
            }
        }

        @CommandLine.Command(name = "list")
        class ListCommand(
            private val docL10nKitAppService: DocL10NKitAppService,
            private val objectMapper: ObjectMapper
        ): Runnable{
            override fun run() {
                val glossaries = docL10nKitAppService.listGlossaries()
                println(objectMapper.writeValueAsString(glossaries))
            }
        }

        @CommandLine.Command(name = "delete")
        class DeleteCommand(private val docL10nKitAppService: DocL10NKitAppService): Runnable{

            private val logger = LoggerFactory.getLogger(this::class.java)

            @CommandLine.Option(order = 1, names = ["--glossaryId"], description = ["glossary id"], required = true)
            private var glossaryId: String? = null

            @CommandLine.Option(order = 9, names = ["--help", "-h"], description = ["print help"], usageHelp = true)
            private var help = false

            override fun run() {
                try{
                    docL10nKitAppService.removeGlossary(glossaryId!!)
                }
                catch (e: DocL10NKitAppException){
                    logger.error("doc-l10n-kit failed with error: ${e.message}", e)
                }
                catch (e: Exception){
                    logger.error("doc-l10n-kit failed with error: ${e.message}\n${e.stackTraceToString()}", e)
                    exitProcess(1)
                }
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