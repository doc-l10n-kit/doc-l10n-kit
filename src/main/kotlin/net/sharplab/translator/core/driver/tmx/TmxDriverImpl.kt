package net.sharplab.translator.core.driver.tmx

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import net.sharplab.translator.core.model.tmx.Tmx
import java.nio.file.Path


class TmxDriverImpl : TmxDriver {

    private val mapper = XmlMapper()

    override fun load(path: Path): Tmx {
        return mapper.readValue(path.toFile(), Tmx::class.java)
    }

    override fun save(tmx: Tmx, path: Path) {
        mapper.writeValue(path.toFile(), tmx)
    }
}
