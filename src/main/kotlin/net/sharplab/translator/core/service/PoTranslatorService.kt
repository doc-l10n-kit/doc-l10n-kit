package net.sharplab.translator.core.service

import net.sharplab.translator.core.model.po.Po

interface PoTranslatorService{

    fun translate(po: Po, source: String, target: String, isAsciidoctor: Boolean = true, glossaryId: String? = null): Po

}