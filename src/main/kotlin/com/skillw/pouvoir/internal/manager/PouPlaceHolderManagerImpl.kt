package com.skillw.pouvoir.internal.manager

import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.manager.sub.PouPlaceHolderManager
import com.skillw.pouvoir.api.placeholder.PouPlaceHolder
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

object PouPlaceHolderManagerImpl : PouPlaceHolderManager() {
    override val key = "PouPlaceHolderManager"
    override val priority = 2
    override val subPouvoir = Pouvoir

    override fun register(key: String, value: PouPlaceHolder) {
        super.register(key, value)
        console().sendLang(
            "pou-placeholder-register",
            key,
            value.name,
            value.version,
            value.author
        )
    }
}