package net.sharplab.translator.core.service

import net.sharplab.translator.core.driver.adoc.AsciidocDriver
import net.sharplab.translator.core.driver.po.PoDriver
import net.sharplab.translator.core.model.po.Po
import org.slf4j.LoggerFactory
import java.nio.file.Path
import javax.inject.Singleton
import kotlin.io.path.exists
import kotlin.io.path.pathString

@Singleton
class AsciidocServiceImpl(private val asciidocDriver: AsciidocDriver, private val poDriver: PoDriver) : AsciidocService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun extract(asciidoc: Path, po: Path, source: String, target: String) {
        logger.info("Begin: extract ${asciidoc.pathString}")
        val asciidocObject = asciidocDriver.load(asciidoc)
        val poObject = when {
            po.exists() -> poDriver.load(po)
            else -> Po(target, emptyList())
        }
        val updatedPo = poObject.update(asciidocObject)
        poDriver.save(updatedPo, po)
        logger.info("End: extract ${asciidoc.pathString}")
    }

    override fun translate(po: Path, sourceAsciidoc: Path, targetAsciidoc: Path) {
        logger.info("Begin: translate ${sourceAsciidoc.pathString}")
        val poFile = poDriver.load(po)
        val asciidoc = asciidocDriver.load(sourceAsciidoc)
        asciidoc.translate(poFile)
        asciidocDriver.save(asciidoc, targetAsciidoc)
        logger.info("End: translate ${sourceAsciidoc.pathString}")
    }
}
