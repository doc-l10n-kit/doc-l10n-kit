package net.sharplab.translator.core.service

import java.nio.file.Path

interface AsciidocService {
    fun extract(asciidoc: Path, po: Path, source: String, target: String)
    fun translate(po: Path, sourceAsciidoc: Path, targetAsciidoc: Path)

}
