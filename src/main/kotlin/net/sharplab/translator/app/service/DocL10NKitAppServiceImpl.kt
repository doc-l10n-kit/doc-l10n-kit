package net.sharplab.translator.app.service

import net.sharplab.translator.core.driver.po.PoReader
import net.sharplab.translator.core.driver.po.PoWriter
import net.sharplab.translator.core.service.PoTranslatorService
import org.jboss.logging.Logger
import java.io.File
import javax.enterprise.context.Dependent

@Dependent
class DocL10NKitAppServiceImpl(private val poTranslatorService: PoTranslatorService) : DocL10NKitAppService {

    private val logger = Logger.getLogger(DocL10NKitAppServiceImpl::class.java)

    private val poReader = PoReader()
    private val poWriter = PoWriter()

    override fun translateAsciiDocPoFile(filePath: File, srcLang: String, dstLang: String, isAsciidoctor: Boolean) {
        logger.info("Start translation: %s".format(filePath.absolutePath))
        val poFile = poReader.read(filePath)
        val translated = poTranslatorService.translate(poFile, srcLang, dstLang, isAsciidoctor)
        poWriter.write(translated, filePath)
        logger.info("Finish translation: %s".format(filePath.absolutePath))
    }
}