package net.sharplab.translator.app.service

import com.deepl.api.GlossaryInfo
import net.sharplab.translator.core.driver.po.PoReader
import net.sharplab.translator.core.driver.po.PoWriter
import net.sharplab.translator.core.driver.translator.DeepLTranslator
import net.sharplab.translator.core.service.PoTranslatorService
import org.jboss.logging.Logger
import java.io.File
import javax.enterprise.context.Dependent

@Dependent
class DocL10NKitAppServiceImpl(private val poTranslatorService: PoTranslatorService, private val deepLTranslator: DeepLTranslator) : DocL10NKitAppService {

    private val logger = Logger.getLogger(DocL10NKitAppServiceImpl::class.java)

    private val poReader = PoReader()
    private val poWriter = PoWriter()

    override fun translateAsciiDocPoFile(
        filePath: File,
        srcLang: String,
        dstLang: String,
        isAsciidoctor: Boolean,
        glossaryId: String?
    ) {
        logger.info("Start translation: %s".format(filePath.absolutePath))
        val poFile = poReader.read(filePath)
        val translated = poTranslatorService.translate(poFile, srcLang, dstLang, isAsciidoctor, glossaryId)
        poWriter.write(translated, filePath)
        logger.info("Finish translation: %s".format(filePath.absolutePath))
    }

    override fun createGlossary(name: String, srcLang: String, dstLang: String, csvFile: File): GlossaryInfo {
        return deepLTranslator.createGlossary(name, srcLang, dstLang, csvFile)
    }

    override fun listGlossaries(): List<GlossaryInfo> {
        return deepLTranslator.listGlossaries()
    }

    override fun removeGlossary(glossaryId: String) {
        return deepLTranslator.removeGlossary(glossaryId)
    }
}