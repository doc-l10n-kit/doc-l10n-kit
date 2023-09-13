package net.sharplab.translator.core.model.adoc

import net.sharplab.translator.core.driver.adoc.AsciidocDriverImpl
import net.sharplab.translator.core.driver.po.PoDriverImpl
import net.sharplab.translator.test.TestUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AsciidocTest {

    private val poDriver = PoDriverImpl()
    private val asciidocDriver = AsciidocDriverImpl()

    @Test
    fun translateAsciidoc_test(){
        val asciidocPath = TestUtil.resolveClasspath("adoc/sample.adoc")
        val poPath= TestUtil.resolveClasspath("po/sample.adoc.po")
        val po = poDriver.load(poPath)
        val asciidoc = asciidocDriver.load(asciidocPath)


        asciidoc.translate(po)
        val result = asciidoc.value

        val expectedAsciidoc = TestUtil.loadFromClasspath("adoc/sample.translated.adoc")
        assertThat(result).isEqualTo(expectedAsciidoc)
    }

    @Test
    fun extractSentences_test(){
        val asciidocPath = TestUtil.resolveClasspath("adoc/test.adoc")

        val asciidoc = asciidocDriver.load(asciidocPath)
        val sentences = asciidoc.sentences
    }
}
