package net.sharplab.translator.core.model.po

import net.sharplab.translator.core.model.adoc.Asciidoc
import net.sharplab.translator.core.model.adoc.SentenceType
import java.io.File

@Suppress("ConvertSecondaryConstructorToPrimary")
class Po {

    val target: String

    val messages: List<PoMessage>

    constructor(target: String, messages: List<PoMessage>){
        this.target = target
        this.messages = messages
    }

    fun update(asciidoc: Asciidoc): Po{
        val messages = asciidoc.sentences
                .filter { item -> item.type != SentenceType.SOURCE }
                .map {
            val relativePath = asciidoc.path.toString().replace(File.separatorChar, '/')
            val sourceReference = "${relativePath}:${it.lineNumber}"
            val message = PoMessageImpl(sourceReference, it.type.value, it.text)
            message.fuzzy = true

            messages.forEach{ existingMessage ->
                val normalizedMessageId = message.messageId.replace(".\n", ".  ").replace('\n', ' ')
                if(existingMessage.messageId == message.messageId || existingMessage.messageId == normalizedMessageId){
                    message.messageString = existingMessage.messageString
                    message.fuzzy = existingMessage.fuzzy
                }
            }

            message
        }

        return Po(target, messages)
    }

}
