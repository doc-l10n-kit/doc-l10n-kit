package net.sharplab.translator.app.service

import com.deepl.api.GlossaryInfo
import java.nio.file.Path

interface DocL10NKitAppService {

    fun machineTranslatePoFile(filePath: Path, source: String, target: String, isAsciidoctor: Boolean, glossaryId: String?)

    fun applyConfirmedTmx(confirmedTmx: Path, po: Path)

    fun applyFuzzyTmx(fuzzyTmx: Path, po: Path)

    fun createGlossary(name: String, source: String, target: String, csvFile: Path): GlossaryInfo

    fun listGlossaries() : List<GlossaryInfo>

    fun removeGlossary(glossaryId: String)

}