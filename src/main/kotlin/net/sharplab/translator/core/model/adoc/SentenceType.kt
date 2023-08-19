package net.sharplab.translator.core.model.adoc

data class SentenceType(val value: String){

    companion object{
        val PLAIN_TEXT = SentenceType("Plain Text")
        val SOURCE = SentenceType("Source")
        val TABLE = SentenceType("Table")
        val TITLE_1 = SentenceType("Title =")
        val TITLE_2 = SentenceType("Title ==")
        val TITLE_3 = SentenceType("Title ===")
        val TITLE_4 = SentenceType("Title ====")
        val TITLE_5 = SentenceType("Title =====")
        val TITLE_6 = SentenceType("Title ======")
        val DELIMITED_BLOCK_1 = SentenceType("delimited block =")
        val DELIMITED_BLOCK_2 = SentenceType("delimited block ==")
        val DELIMITED_BLOCK_3 = SentenceType("delimited block ===")
        val DELIMITED_BLOCK_4 = SentenceType("delimited block ====")
        val DELIMITED_BLOCK_5 = SentenceType("delimited block =====")
        val YAML_FRONT_MATTER = SentenceType("YAML Front Matter")
    }
}
