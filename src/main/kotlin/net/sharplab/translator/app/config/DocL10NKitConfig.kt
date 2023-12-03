package net.sharplab.translator.app.config

import net.sharplab.translator.app.setting.DocL10NKitSetting
import net.sharplab.translator.core.driver.po.PoDriver
import net.sharplab.translator.core.driver.po.PoDriverImpl
import net.sharplab.translator.core.driver.tmx.TmxDriver
import net.sharplab.translator.core.driver.tmx.TmxDriverImpl
import net.sharplab.translator.core.driver.translator.DeepLTranslator
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces

@Dependent
class DocL10NKitConfig(private val asciiDocPoTranslatorSetting: DocL10NKitSetting) {

    @Produces
    fun deeplTranslator(): DeepLTranslator
    {
        val deepLApiKey = asciiDocPoTranslatorSetting.deepLApiKey.orElse("")
        return DeepLTranslator(deepLApiKey)
    }

    @Produces
    fun poDriver() : PoDriver{
        return PoDriverImpl()
    }

    @Produces
    fun tmxDriver() : TmxDriver{
        return TmxDriverImpl()
    }

}
