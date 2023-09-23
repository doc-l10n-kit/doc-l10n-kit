package net.sharplab.translator.core.model.tmx

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Tmx @JsonCreator constructor(
        @JacksonXmlProperty(isAttribute = true, localName = "version") val version: String,
        @JacksonXmlProperty(isAttribute = false, localName = "header") val tmxHeader: TmxHeader,
        @JacksonXmlProperty(isAttribute = false, localName = "body") val tmxBody: TmxBody) {
}