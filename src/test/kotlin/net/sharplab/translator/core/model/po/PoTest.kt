package net.sharplab.translator.core.model.po

import net.sharplab.translator.core.driver.adoc.AsciidocDriverImpl
import net.sharplab.translator.core.driver.po.PoDriverImpl
import net.sharplab.translator.test.TestUtil
import org.junit.jupiter.api.Test

class PoTest {

    private val poDriver = PoDriverImpl()
    private val asciidocDriver = AsciidocDriverImpl()

    @Test
    fun updatePo_test(){
        val asciidocPath = TestUtil.resolveClasspath("adoc/sample.adoc")
        val poPath= TestUtil.resolveClasspath("po/sample.adoc.po")

        val po = poDriver.load(poPath)
        val asciidoc = asciidocDriver.load(asciidocPath)

        val updatedPo = po.update(asciidoc)
        poDriver.save(updatedPo, poPath)
    }
}
