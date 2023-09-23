package net.sharplab.translator.core.model.tmx

import net.sharplab.translator.core.driver.tmx.TmxDriverImpl
import net.sharplab.translator.test.TestUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TranslationIndexTest{

    @Test
    fun test(){
        val tmxPath = TestUtil.resolveClasspath("tmx/test.tmx")
        val tmx = TmxDriverImpl().load(tmxPath)
        val index = TranslationIndex.create(tmx, "ja_JP")
        
    }

}