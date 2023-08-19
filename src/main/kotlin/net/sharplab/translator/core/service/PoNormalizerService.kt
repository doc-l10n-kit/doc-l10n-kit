package net.sharplab.translator.core.service

import net.sharplab.translator.core.model.po.Po

interface PoNormalizerService {

    fun normalize(po: Po): Po
}