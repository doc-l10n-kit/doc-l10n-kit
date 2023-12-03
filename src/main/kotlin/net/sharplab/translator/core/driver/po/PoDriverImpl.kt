package net.sharplab.translator.core.driver.po

import net.sharplab.translator.core.model.po.Po
import net.sharplab.translator.core.model.po.PoMessageImpl
import org.fedorahosted.tennera.jgettext.Catalog
import org.fedorahosted.tennera.jgettext.Message
import org.fedorahosted.tennera.jgettext.PoParser
import org.fedorahosted.tennera.jgettext.PoWriter
import java.io.FileOutputStream
import java.nio.file.Path

class PoDriverImpl : PoDriver {
    override fun load(path: Path): Po {
        val poParser = PoParser()
        val catalog = poParser.parseCatalog(path.toFile())
        val target = "ja_JP" //TODO
        val messages = catalog.map {item -> PoMessageImpl(item) }
        return Po(target, messages)
    }

    override fun save(po: Po, path: Path) {
        FileOutputStream(path.toFile()).use { outputStream ->
            val poWriter = PoWriter()
            val catalog = Catalog()

            val headerMessage = Message()
            val headerValues = """
            Language: ${po.target}
            MIME-Version: 1.0
            Content-Type: text/plain; charset=UTF-8
            Content-Transfer-Encoding: 8bit
            X-Generator: doc-l10n-kit
        """.trimIndent() + "\n"
            headerMessage.msgid = ""
            headerMessage.msgstr = headerValues
            catalog.addMessage(headerMessage)

            po.messages.filter { it.messageId.isNotEmpty() }.forEach { item ->
                val messageImpl = item as PoMessageImpl
                catalog.addMessage(messageImpl.message)
            }

            poWriter.write(catalog, outputStream)
        }
    }
}