package com.skillw.pouvoir.api.manager.sub.script

import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap
import com.skillw.pouvoir.internal.script.common.PouCompiledScript
import java.io.File

/**
 * Script manager
 *
 * @constructor Create empty Script manager
 */
abstract class ScriptManager : Manager, KeyMap<String, PouCompiledScript>() {
    /**
     * Add script
     *
     * @param file 单个脚本文件
     */
    abstract fun addScript(file: File)

    /**
     * Add script dir
     *
     * @param file 脚本文件夹
     */
    abstract fun addScriptDir(file: File)

    /**
     * Search
     *
     * @param path 路径
     * @param silent 如有错是否报
     * @return 查找到的预编译脚本
     */
    abstract fun search(path: String, silent: Boolean = false): PouCompiledScript?

    /**
     * Invoke
     *
     * @param pathWithFunction 路径::函数名
     * @param arguments 参数
     * @param parameters 函数参数
     * @param T 返回类型
     * @return 返回值
     */
    abstract fun <T> invoke(
        pathWithFunction: String,
        arguments: Map<String, Any> = emptyMap(),
        vararg parameters: Any?,
    ): T?

    /**
     * Invoke
     *
     * @param path 路径
     * @param function 函数名
     * @param arguments 参数
     * @param parameters 函数参数
     * @param T 返回类型
     * @return 返回值
     */
    abstract fun <T> invoke(
        path: String,
        function: String = "main",
        arguments: Map<String, Any> = emptyMap(),
        vararg parameters: Any?,
    ): T?

    /**
     * Invoke
     *
     * @param script 预编译脚本
     * @param function 函数名
     * @param arguments 参数
     * @param parameters 函数参数
     * @param T 返回类型
     * @return 返回值
     */
    abstract fun <T> invoke(
        script: PouCompiledScript,
        function: String = "main",
        arguments: Map<String, Any> = emptyMap(),
        vararg parameters: Any?,
    ): T?
}