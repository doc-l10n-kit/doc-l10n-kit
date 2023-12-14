package net.sharplab.translator.core.model.tmx

import net.sharplab.translator.core.driver.tmx.TmxDriverImpl
import net.sharplab.translator.test.TestUtil
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

class TranslationIndexTest{

    @Test
    fun tmx_test(){
        val tmxPath = TestUtil.resolveClasspath("tmx/test.tmx")
        val tmx = TmxDriverImpl().load(tmxPath)

        assertThatCode {
            val index = TranslationIndex.create(tmx, "ja_JP")
        }.doesNotThrowAnyException()

    }

    @Test
    fun fuzzy_tmx_test(){
        val tmxPath = TestUtil.resolveClasspath("tmx/fuzzy.tmx")
        val tmx = TmxDriverImpl().load(tmxPath)

        assertThatCode {
            val index = TranslationIndex.create(tmx, "es_ES")
        }.doesNotThrowAnyException()

    }

}