package net.sharplab.translator.core.model.tmx

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class TranslationUnitVariant @JsonCreator constructor(
        @JacksonXmlProperty(isAttribute = true, localName= "lang") val lang: String,
        @JacksonXmlProperty(isAttribute = false, localName = "seg") val seg: String) {
}