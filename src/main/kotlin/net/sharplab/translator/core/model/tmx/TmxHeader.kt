package net.sharplab.translator.core.model.tmx

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class TmxHeader @JsonCreator constructor(
        @JacksonXmlProperty(isAttribute = true, localName="creationtool")
        val creationTool: String,

        @JacksonXmlProperty(isAttribute = true, localName="creationtoolversion")
        val creationToolVersion: String,

        @JacksonXmlProperty(isAttribute = true, localName="segtype")
        val segType: String,

        @JacksonXmlProperty(isAttribute = true, localName="o-tmf")
        val oTmf: String,

        @JacksonXmlProperty(isAttribute = true, localName="adminlang")
        val adminLang: String,

        @JacksonXmlProperty(isAttribute = true, localName="srclang")
        val srcLang: String,

        @JacksonXmlProperty(isAttribute = true, localName="datatype")
        val dataType: String
)
