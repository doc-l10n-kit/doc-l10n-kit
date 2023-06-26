package net.sharplab.translator.app.service

import com.deepl.api.GlossaryInfo
import java.io.File

interface DocL10NKitAppService {

    fun translateAsciiDocPoFile(filePath: File, srcLang: String, dstLang: String, isAsciidoctor: Boolean = true, glossaryId: String? = null)

    fun createGlossary(name: String, srcLang: String, dstLang: String, csvFile: File): GlossaryInfo

    fun listGlossaries() : List<GlossaryInfo>

    fun removeGlossary(glossaryId: String)

}