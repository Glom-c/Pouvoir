package com.skillw.pouvoir.util

import taboolib.module.chat.colored
import taboolib.module.chat.uncolored

/**
 * ClassName : com.skillw.classsystem.util.ColorUtils
 * Created by Glom_ on 2021-03-25 20:26:01
 * Copyright  2021 user. All rights reserved.
 */
object ColorUtils {
    @JvmStatic
    fun color(msg: String): String {
        return msg.colored()
    }

    @JvmStatic
    fun color(messages: List<String>): List<String> {
        return messages.colored()
    }

    @JvmStatic
    fun unColor(msg: String): String {
        return msg.uncolored()
    }

    @JvmStatic
    fun unColor(messages: List<String>): List<String> {
        return messages.uncolored()
    }

    @JvmStatic
    fun String.decolored(): String {
        return this.replace("§", "&")
    }

    @JvmStatic
    fun List<String>.decolored(): List<String> {
        return this.map { it.decolored() }
    }
}