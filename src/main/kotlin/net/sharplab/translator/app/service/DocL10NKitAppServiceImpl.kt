package net.sharplab.translator.app.service

import com.deepl.api.GlossaryInfo
import net.sharplab.translator.app.exception.DocL10NKitAppException
import net.sharplab.translator.app.setting.DocL10NKitSetting
import net.sharplab.translator.core.driver.po.PoDriver
import net.sharplab.translator.core.driver.translator.DeepLTranslator
import net.sharplab.translator.core.service.AsciidocService
import net.sharplab.translator.core.service.PoTranslatorService
import org.jboss.logging.Logger
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import javax.enterprise.context.Dependent
import kotlin.io.path.*


@Dependent
class DocL10NKitAppServiceImpl(
        private val poTranslatorService: PoTranslatorService,
        private val asciidocService: AsciidocService,
        private val deepLTranslator: DeepLTranslator,
        private val poDriver: PoDriver,
        private val asciiDocPoTranslatorSetting: DocL10NKitSetting) : DocL10NKitAppService {

    private val logger = Logger.getLogger(DocL10NKitAppServiceImpl::class.java)


    override fun extract(asciidoc: Path, po: Path, source: String?, target: String?) {
        if(!asciidoc.exists()){
            throw DocL10NKitAppException("asciidoc file does not exist")
        }
        if(asciidoc.isDirectory()) {
            if (po.exists() && po.isRegularFile()) {
                throw DocL10NKitAppException("When asciidoc parameter takes a directory, po parameter must take a directory too.")
            }
        }
        val resolvedSourceLang = source ?: asciiDocPoTranslatorSetting.defaultSourceLang
        val resolvedTargetLang = target ?: asciiDocPoTranslatorSetting.defaultTargetLang

        val fs: FileSystem = FileSystems.getDefault()
        val globPattern = fs.getPathMatcher("glob:**/*.adoc")
        val excludePattern = fs.getPathMatcher("glob:**/_generated-doc/**.adoc")

        val files = Files.walk(asciidoc)
                .filter(globPattern::matches)
                .filter{ !it.isDirectory() }
                .filter{ !excludePattern.matches(it) }

        files.forEach{ asciidocPath ->
            val relativeAsciidocPath = asciidocPath.relativeTo(asciidoc)
            val resolvedPoPath = Path(po.resolve(relativeAsciidocPath).pathString + ".po")
            if (!resolvedPoPath.parent.exists()) {
                Files.createDirectories(resolvedPoPath.parent)
            }
            try {
                asciidocService.extract(asciidocPath, resolvedPoPath, resolvedSourceLang, resolvedTargetLang)
            } catch (e: RuntimeException) {
                throw DocL10NKitAppException("Failed to extract sentences from an asciidoc: ${asciidoc.absolutePathString()}", e)
            }
        }

    }

    override fun translateAsciidoc(
            po: Path,
            sourceAsciidoc: Path,
            targetAsciidoc: Path
    ) {
        if(!sourceAsciidoc.exists()){
            throw DocL10NKitAppException("sourceAsciidoc file does not exist")
        }
        if(sourceAsciidoc.isDirectory()) {
            if (targetAsciidoc.exists() && targetAsciidoc.isRegularFile()) {
                throw DocL10NKitAppException("When sourceAsciidoc parameter takes a directory, targetAsciidoc parameter must take a directory too.")
            }
            if (po.exists() && po.isRegularFile()) {
                throw DocL10NKitAppException("When sourceAsciidoc parameter takes a directory, po parameter must take a directory too.")
            }
        }

        val fs: FileSystem = FileSystems.getDefault()
        val globPattern = fs.getPathMatcher("glob:**/*.adoc")
        val excludePattern = fs.getPathMatcher("glob:**/_generated-doc/**.adoc")

        val files = Files.walk(sourceAsciidoc)
                .filter(globPattern::matches)
                .filter{ !it.isDirectory() }
                .filter{ !excludePattern.matches(it) }
        files.forEach { sourceAsciidocPath ->
            if (!targetAsciidoc.parent.exists()) {
                Files.createDirectories(targetAsciidoc.parent)
            }
            val relativeAsciidocPath = sourceAsciidocPath.relativeTo(sourceAsciidoc)
            val resolvedPoPath = Path(po.resolve(relativeAsciidocPath).pathString + ".po")
            val resolvedTargetAsciidocPath = targetAsciidoc.resolve(relativeAsciidocPath)
            try {
                asciidocService.translate(resolvedPoPath, sourceAsciidocPath, resolvedTargetAsciidocPath)
            } catch (e: RuntimeException) {
                throw DocL10NKitAppException("Failed to translate asciidoc: ${sourceAsciidoc.absolutePathString()}", e)
            }
        }

    }

    override fun machineTranslatePoFile(filePath: Path, source: String, target: String, isAsciidoctor: Boolean, glossaryId: String?) {
        logger.info("Start translation: %s".format(filePath.absolutePathString()))
        val poFile = poDriver.load(filePath)
        val translated = poTranslatorService.translate(poFile, source, target, isAsciidoctor, glossaryId)
        poDriver.save(translated, filePath)
        logger.info("Finish translation: %s".format(filePath.absolutePathString()))
    }

    override fun createGlossary(name: String, source: String, target: String, csvFile: Path): GlossaryInfo {
        return deepLTranslator.createGlossary(name, source, target, csvFile)
    }

    override fun listGlossaries(): List<GlossaryInfo> {
        return deepLTranslator.listGlossaries()
    }

    override fun removeGlossary(glossaryId: String) {
        return deepLTranslator.removeGlossary(glossaryId)
    }

}