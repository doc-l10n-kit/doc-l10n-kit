package net.sharplab.translator.core.model

import org.fedorahosted.tennera.jgettext.Message

@Suppress("ConvertSecondaryConstructorToPrimary")
class PoMessage{

    val message: Message

    constructor(message: Message) {
        this.message = message
    }

    val type: String
        get(){
            return this.message.extractedComments.firstOrNull{ it.startsWith("type:") }?: MessageType.None
        }

    val messageId: String
        get() = message.msgid

    var messageString: String
        get() = message.msgstr
        set(value){
            message.msgstr = value
        }

    var fuzzy: Boolean
        get() = message.isFuzzy
        set(value){
            message.isFuzzy = value
        }
}