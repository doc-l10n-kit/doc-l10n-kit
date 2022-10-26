package net.sharplab.translator.app.config

import net.sharplab.translator.app.setting.DocL10NKitSetting
import net.sharplab.translator.core.driver.translator.DeepLTranslator
import net.sharplab.translator.core.driver.translator.Translator
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces

@Dependent
class DocL10NKitConfig(private val asciiDocPoTranslatorSetting: DocL10NKitSetting) {

    @Produces
    fun translator(): Translator
    {
        val deepLApiKey = asciiDocPoTranslatorSetting.deepLApiKey
        return DeepLTranslator(deepLApiKey.get())
    }
}
