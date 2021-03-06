package com.skillw.pouvoir.internal.hook

import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.Pouvoir.scriptManager
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.placeholder.PouPlaceHolder
import com.skillw.pouvoir.util.Pair
import com.skillw.pouvoir.util.StringUtils.protectedSplit
import com.skillw.pouvoir.util.StringUtils.toArgs
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.platform.compat.replacePlaceholder

@AutoRegister
object PouvoirHooker : PouPlaceHolder("pou", Pouvoir) {
    override fun onPlaceHolderRequest(params: String, entity: LivingEntity, def: String): String {
        if (params.isBlank()) return def
        var argsStr = params.replace("@", "%")
        if (entity is Player) {
            argsStr = argsStr.replacePlaceholder(entity)
        }
        val args = argsStr.protectedSplit('_', Pair('[', ']'))
        if (args.isEmpty()) return def
        when (args[0].lowercase()) {
            "run" -> {
                args.removeAt(0)
                if (args.isEmpty()) return def
                val scriptPath = args[0]
                val spilt: Array<String> = if (args.size > 1) params.replace("run_", "").toArgs() else emptyArray()
                val finalArgs = Array(spilt.size) {
                    Pouvoir.pouPlaceHolderAPI.replace(entity, spilt[it])
                }
                return scriptManager.invoke<String>(
                    scriptPath,
                    arguments = hashMapOf("entity" to entity, "args" to finalArgs)
                ).toString()
            }
        }
        return def
    }


}
