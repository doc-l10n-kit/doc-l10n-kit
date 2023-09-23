package net.sharplab.translator.core.model.tmx

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class TmxBody @JsonCreator constructor(
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(isAttribute = false, localName = "tu")
        val translationUnits: List<TranslationUnit>
)
