package net.sharplab.translator.core.model.po

import java.io.File

@Suppress("ConvertSecondaryConstructorToPrimary")
interface PoMessage{

    val type: String
    val messageId: String
    var messageString: String
    val sourceReferences: List<SourceReference>
    var fuzzy: Boolean


    data class SourceReference(val file: File, val lineNumber: Int)

}
