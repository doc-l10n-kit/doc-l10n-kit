package net.sharplab.translator.core.model.po

import org.fedorahosted.tennera.jgettext.Message
import java.io.File

@Suppress("ConvertSecondaryConstructorToPrimary")
class PoMessageImpl : PoMessage {

    val message: Message

    constructor(message: Message) {
        this.message = message
    }

    constructor(sourceReference: String, type: String, messageId: String){
        this.message = Message()
        this.message.msgid = messageId
        this.message.sourceReferences.add(sourceReference)
        this.message.extractedComments.add("type: $type")
    }

    constructor(text: String){
        this.message = Message()
        this.message.msgid = text
    }

    override val type: String
        get(){
            return this.message.extractedComments.firstOrNull{ it.startsWith("type:") }?: MessageType.None
        }

    override val messageId: String
        get() = message.msgid

    override var messageString: String
        get() = message.msgstr
        set(value){
            message.msgstr = value
        }
    override val sourceReferences: List<PoMessage.SourceReference>
        get(){
            return this.message.sourceReferences.map {
                val splitted = it.split(':')
                if(splitted.size != 2){
                    TODO()
                }
                val file = File(splitted.first())
                val lineNumber = splitted.last().toInt()
                PoMessage.SourceReference(file, lineNumber)
            }
        }

    override var fuzzy: Boolean
        get() = message.isFuzzy
        set(value){
            message.isFuzzy = value
        }
}