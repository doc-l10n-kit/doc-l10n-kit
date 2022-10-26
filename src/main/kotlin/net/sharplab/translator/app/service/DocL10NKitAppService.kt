package net.sharplab.translator.app.service

import java.io.File

interface DocL10NKitAppService {

    fun translateAsciiDocPoFile(filePath: File, srcLang: String, dstLang: String, isAsciidoctor: Boolean = true)

}