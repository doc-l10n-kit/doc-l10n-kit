package net.sharplab.translator.app.service

import com.deepl.api.GlossaryInfo
import net.sharplab.translator.app.setting.DocL10NKitSetting
import net.sharplab.translator.core.driver.po.PoDriver
import net.sharplab.translator.core.driver.tmx.TmxDriver
import net.sharplab.translator.core.driver.translator.DeepLTranslator
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
        private val deepLTranslator: DeepLTranslator,
        private val poDriver: PoDriver,
        private val tmxDriver: TmxDriver,
        private val asciiDocPoTranslatorSetting: DocL10NKitSetting) : DocL10NKitAppService {

    private val logger = Logger.getLogger(DocL10NKitAppServiceImpl::class.java)

    override fun machineTranslatePoFile(filePath: Path, source: String, target: String, isAsciidoctor: Boolean, glossaryId: String?) {
        logger.info("Start translation: %s".format(filePath.absolutePathString()))
        val poFile = poDriver.load(filePath)
        val translated = poTranslatorService.translate(poFile, source, target, isAsciidoctor, glossaryId)
        poDriver.save(translated, filePath)
        logger.info("Finish translation: %s".format(filePath.absolutePathString()))
    }

    override fun applyConfirmedTmx(confirmedTmx: Path, po: Path) {
        fun doApplyTmx(poPath: Path){
            logger.info("Start apply tmx: %s".format(poPath.absolutePathString()))
            val tmxFile = tmxDriver.load(confirmedTmx)
            val poFile = poDriver.load(poPath)
            val translated = poTranslatorService.applyTmx(tmxFile, poFile)
            poDriver.save(translated, poPath)
            logger.info("Finish apply tmx: %s".format(poPath.absolutePathString()))
        }

        val fs: FileSystem = FileSystems.getDefault()
        val globPattern = fs.getPathMatcher("glob:**/*.po")

        Files.walk(po)
                .filter(globPattern::matches)
                .filter{ !it.isDirectory() }
                .forEach(::doApplyTmx)
    }

    override fun applyFuzzyTmx(fuzzyTmx: Path, po: Path) {
        fun doApplyTmx(poPath: Path){
            logger.info("Start apply fuzzy tmx: %s".format(poPath.absolutePathString()))
            val fuzzyTmxFile = tmxDriver.load(fuzzyTmx)
            val poFile = poDriver.load(poPath)
            val translated = poTranslatorService.applyFuzzyTmx(fuzzyTmxFile, poFile)
            poDriver.save(translated, poPath)
            logger.info("Finish apply fuzzy tmx: %s".format(poPath.absolutePathString()))
        }

        val fs: FileSystem = FileSystems.getDefault()
        val globPattern = fs.getPathMatcher("glob:**/*.po")

        Files.walk(po)
                .filter(globPattern::matches)
                .filter{ !it.isDirectory() }
                .forEach(::doApplyTmx)
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