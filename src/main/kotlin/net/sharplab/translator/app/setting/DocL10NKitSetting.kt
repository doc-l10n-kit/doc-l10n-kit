package net.sharplab.translator.app.setting

import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.*
import javax.enterprise.context.Dependent

@Dependent
class DocL10NKitSetting {
    @ConfigProperty(name = "translator.deepL.apiKey")
    lateinit var deepLApiKey: Optional<String>
    @ConfigProperty(name = "translator.language.source", defaultValue = "en")
    lateinit var defaultSourceLang: String
    @ConfigProperty(name = "translator.language.destination", defaultValue = "ja")
    lateinit var defaultTargetLang: String
}
