package net.sharplab.translator.core.driver.tmx

import net.sharplab.translator.test.TestUtil
import org.junit.jupiter.api.Test

class TmxDriverImplTest{

    val target = TmxDriverImpl()

    @Test
    fun test(){
        val tmxPath = TestUtil.resolveClasspath("tmx/test.tmx")
        val tmx = target.load(tmxPath)
    }


}