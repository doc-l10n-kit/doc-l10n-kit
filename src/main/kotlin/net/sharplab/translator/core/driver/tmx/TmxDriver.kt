package net.sharplab.translator.core.driver.tmx

import net.sharplab.translator.core.model.tmx.Tmx
import java.nio.file.Path

interface TmxDriver {
    fun load(path: Path): Tmx

    fun save(tmx: Tmx, path: Path)
}