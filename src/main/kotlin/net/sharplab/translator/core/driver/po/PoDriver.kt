package net.sharplab.translator.core.driver.po

import net.sharplab.translator.core.model.po.Po
import java.nio.file.Path

interface PoDriver {

    fun load(path: Path): Po

    fun save(po: Po, path: Path)
}