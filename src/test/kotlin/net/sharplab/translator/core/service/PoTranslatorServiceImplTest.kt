package net.sharplab.translator.core.service

import net.sharplab.translator.core.driver.po.PoDriverImpl
import net.sharplab.translator.core.driver.tmx.TmxDriverImpl
import net.sharplab.translator.core.driver.translator.Translator
import net.sharplab.translator.test.TestUtil
import org.junit.jupiter.api.Test

internal class PoTranslatorServiceImplTest{

    private val target = PoTranslatorServiceImpl(object : Translator {
        override fun translate(texts: List<String>, srcLang: String, dstLang: String, glossaryId: String?): List<String> {
            TODO("not implemented")
        }
    })

    @Test
    fun translateHeader_test(){
        val poPath = TestUtil.resolveClasspath("po/fuzzy.adoc.po")
        val po = PoDriverImpl().load(poPath)
        val fuzzyTmxPath = TestUtil.resolveClasspath("tmx/fuzzy.tmx")
        val fuzzyTmx = TmxDriverImpl().load(fuzzyTmxPath)

        val updatedPo = target.applyFuzzyTmx(fuzzyTmx, po)
    }

}