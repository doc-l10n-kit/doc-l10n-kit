package net.sharplab.translator.app.service

import com.deepl.api.GlossaryInfo
import net.sharplab.translator.app.exception.DocL10NKitAppException
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
    private val tmxDriver: TmxDriver
) : DocL10NKitAppService {

    private val logger = Logger.getLogger(DocL10NKitAppServiceImpl::class.java)

    override fun machineTranslatePoFile(filePath: Path, source: String, target: String, isAsciidoctor: Boolean, glossaryId: String?) {
        logger.info("Start translation: %s".format(filePath.absolutePathString()))
        val poFile = poDriver.load(filePath)
        val translated = poTranslatorService.translate(poFile, source, target, isAsciidoctor, glossaryId)
        poDriver.save(translated, filePath)
        logger.info("Finish translation: %s".format(filePath.absolutePathString()))
    }

    override fun applyTmx(tmx: Path, po: Path) {
        val tmxFile = tmxDriver.load(tmx)

        fun doApplyTmx(poPath: Path){
            try{
                logger.info("Start applying tmx: %s".format(poPath.absolutePathString()))
                val poFile = poDriver.load(poPath)
                val translated = poTranslatorService.applyTmx(tmxFile, poFile)
                poDriver.save(translated, poPath)
                logger.info("Finish applying tmx: %s".format(poPath.absolutePathString()))
            }
            catch(e: RuntimeException){
                throw DocL10NKitAppException("Failed applying tmx: %s".format(poPath.absolutePathString()), e)
            }
        }

        val fs: FileSystem = FileSystems.getDefault()
        val globPattern = fs.getPathMatcher("glob:**/*.po")

        Files.walk(po)
                .filter(globPattern::matches)
                .filter{ !it.isDirectory() }
                .parallel()
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