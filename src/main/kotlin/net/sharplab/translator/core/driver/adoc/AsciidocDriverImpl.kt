package net.sharplab.translator.core.driver.adoc

import net.sharplab.translator.core.model.adoc.Asciidoc
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.writeText

class AsciidocDriverImpl : AsciidocDriver {

    override fun load(asciidoc: Path): Asciidoc {
        return AsciidocParser.parse(asciidoc)
    }

    override fun save(asciidoc: Asciidoc, path: Path) {
        path.writeText(asciidoc.value, StandardCharsets.UTF_8)
    }


}