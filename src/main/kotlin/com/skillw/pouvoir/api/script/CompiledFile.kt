package com.skillw.pouvoir.api.script

import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.Pouvoir.scriptManager
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.manager.Manager.Companion.ENABLE
import com.skillw.pouvoir.api.manager.Manager.Companion.RELOAD
import com.skillw.pouvoir.api.manager.Manager.Companion.addSingle
import com.skillw.pouvoir.api.map.SingleExecMap
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import com.skillw.pouvoir.api.script.engine.PouScriptEngine
import com.skillw.pouvoir.internal.engine.JavaScriptEngine
import com.skillw.pouvoir.util.FileUtils
import com.skillw.pouvoir.util.MessageUtils.wrong
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.script.CompiledScript

class CompiledFile(val file: File, val subPouvoir: SubPouvoir) : Keyable<String>, SingleExecMap() {

    override val key = FileUtils.pathNormalize(file)
    private val pouScriptEngine: PouScriptEngine by lazy {
        Pouvoir.scriptEngineManager.getEngine(file.extension)
            ?: run { wrong("Pouvoir hasn't supported the script files with extension ${file.extension}!"); JavaScriptEngine }
    }
    private var compiledScript: CompiledScript? = Pouvoir.compileManager.compileFile(file)

    // Function to Annotations
    private val annotations = ConcurrentHashMap<String, LinkedList<ScriptAnnotationData>>()
    val canCompiled: Boolean
        get() = compiledScript != null

    val functions by lazy {
        val set = HashSet<String>()
        set.addAll(annotations.keys().toList())
        return@lazy set
    }

    init {
        init()
    }

    private fun init() {
        recompile()
        compiledScript ?: run {
            wrong("CompiledScript is null in $key!")
            return
        }
        val script = file.readLines()
        annotations.clear()
        annotations.putAll(pouScriptEngine.getAnnotationData(this, script))
    }

    fun invoke(function: String, argsMap: MutableMap<String, Any> = HashMap(), vararg args: Any): Any? {
        compiledScript ?: run {
            wrong("$key 's compiled script is null!")
            return null
        }
        return scriptManager.invoke(compiledScript!!, function, argsMap, key, *args)
    }

    override fun run(thing: String) {
        this.filter {
            it.key.startsWith(thing.lowercase())
        }.forEach {
            it.value.invoke()
            this.remove(it.key)
        }
    }

    private fun recompile() {
        run("recompile")
        compiledScript = Pouvoir.compileManager.compileFile(file)
    }

    fun reload() {
        run(RELOAD)
        init()
        register()
    }

    override fun register() {
        run("register")
        compiledScript ?: run {
            wrong("$key 's compiled script is null!")
            return
        }
        annotations.values.forEach {
            data@ for (data in it) {
                val annotation = Pouvoir.scriptAnnotationManager[data.annotation] ?: continue@data
                if (annotation.awakeWhenEnable && !subPouvoir.plugin.isEnabled) {
                    scriptManager.addSingle(ENABLE) {
                        annotation.handle(data)
                    }
                    continue@data
                }
                annotation.handle(data)
            }
        }
        scriptManager[key] = this
    }
}