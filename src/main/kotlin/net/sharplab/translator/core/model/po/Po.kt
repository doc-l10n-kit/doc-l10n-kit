package net.sharplab.translator.core.model.po

@Suppress("ConvertSecondaryConstructorToPrimary")
class Po {

    val target: String

    val messages: List<PoMessage>

    constructor(target: String, messages: List<PoMessage>){
        this.target = target
        this.messages = messages
    }

}
