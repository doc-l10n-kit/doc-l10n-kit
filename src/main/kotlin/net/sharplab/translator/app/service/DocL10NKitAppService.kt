package net.sharplab.translator.app.service

import com.deepl.api.GlossaryInfo
import java.io.File
import java.nio.file.Path

interface DocL10NKitAppService {

    fun extract(asciidoc: Path, po: Path, source: String?, target: String?)

    fun translateAsciidoc(po: Path, sourceAsciidoc: Path, targetAsciidoc: Path)

    fun machineTranslatePoFile(filePath: Path, source: String, target: String, isAsciidoctor: Boolean, glossaryId: String?)

    fun createGlossary(name: String, source: String, target: String, csvFile: Path): GlossaryInfo

    fun listGlossaries() : List<GlossaryInfo>

    fun removeGlossary(glossaryId: String)

}