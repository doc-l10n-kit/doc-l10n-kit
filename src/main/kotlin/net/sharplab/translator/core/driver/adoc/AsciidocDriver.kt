package net.sharplab.translator.core.driver.adoc

import net.sharplab.translator.core.model.adoc.Asciidoc
import java.nio.file.Path

interface AsciidocDriver {

    fun save(asciidoc: Asciidoc, path: Path)
    fun load(asciidoc: Path): Asciidoc
}
