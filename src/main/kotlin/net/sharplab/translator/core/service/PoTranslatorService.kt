package net.sharplab.translator.core.service

import net.sharplab.translator.core.model.po.Po
import net.sharplab.translator.core.model.tmx.Tmx

interface PoTranslatorService{

    fun translate(po: Po, source: String, target: String, isAsciidoctor: Boolean = true, glossaryId: String? = null): Po
    fun applyTmx(tmx: Tmx, po: Po): Po
    fun applyFuzzyTmx(fuzzyTmx: Tmx, po: Po): Po


}