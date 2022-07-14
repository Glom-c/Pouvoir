package com.skillw.pouvoir.internal.hologram

import com.skillw.pouvoir.util.EntityUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import taboolib.common.reflect.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass
import taboolib.module.nms.obcClass
import taboolib.module.nms.sendPacket
import java.util.*
import java.util.function.Consumer

class PouArmorStand(val id: Int, var location: Location, consumer: Consumer<PouArmorStand>) {
    var isDeleted: Boolean = false
    val uniqueId by lazy {
        armorStand.getProperty<UUID>(if (!MinecraftVersion.isUniversal) "uniqueID" else "aj").toString()
            .replace("-", "")


    }
    private var armorStand: Any =
        nmsClass("EntityArmorStand")
            .invokeConstructor(
                obcClass("CraftWorld").cast(location.world).invokeMethod("getHandle"),
                location.x,
                location.y,
                location.z
            )

    init {
        armorStand.invokeMethod<Unit>("e", id)
        consumer.accept(this)
    }

    /**
     * 标准化 UUID
     */
    val normalizeUniqueId: UUID
        get() {
            val u = uniqueId
            return UUID.fromString(
                "${u.substring(0, 8)}-${u.substring(8, 12)}-${u.substring(12, 16)}-${
                    u.substring(
                        16,
                        20
                    )
                }-${u.substring(20)}"
            )
        }


    fun setSmall(value: Boolean) {
        if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Unit>("a", value)
        } else
            armorStand.invokeMethod<Unit>("setSmall", value)
    }

    fun isSmall(): Boolean {
        return if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Boolean>("n") == true
        } else
            armorStand.invokeMethod<Boolean>("isSmall") == true
    }

    fun setGravity(value: Boolean) {
        if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Unit>("e", value)

        } else
            armorStand.invokeMethod<Unit>("setGravity", value)
    }

    fun isGravity(): Boolean {
        return if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Boolean>("aM") == true
        } else
            armorStand.invokeMethod<Boolean>("hasGravity") == true
    }

    fun setArms(value: Boolean) {
        if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Unit>("r", value)
        } else
            armorStand.invokeMethod<Unit>("setArms", value)
    }

    fun hasArms(): Boolean {
        return if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Boolean>("q") == true

        } else
            armorStand.invokeMethod<Boolean>("hasArms") == true
    }

    fun setBasePlate(value: Boolean) {
        if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Unit>("s", !value)

        } else
            armorStand.invokeMethod<Unit>("setBasePlate", !value)
    }

    fun hasBasePlate(): Boolean {
        return if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Boolean>("r") == true

        } else
            armorStand.invokeMethod<Boolean>("noBasePlate") == true
    }

    fun setMarker(value: Boolean) {
        if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Unit>("t", value)
        } else
            armorStand.invokeMethod<Unit>("setMarker", value)
    }

    fun isMarker(): Boolean {
        return if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Boolean>("t") == true
        } else
            armorStand.invokeMethod<Boolean>("isMarker") == true
    }

    fun setInvisible(value: Boolean) {
        if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Unit>("j", value)
        } else
            armorStand.invokeMethod<Unit>("setInvisible", value)
    }

    fun getCustomName(): String {
        return armorStand.invokeMethod<String>(if (MinecraftVersion.majorLegacy >= 11800) "Z" else "getCustomName")!!
    }

    fun setCustomName(name: String) {
        if (MinecraftVersion.majorLegacy < 11300)
            armorStand.invokeMethod<Unit>("setCustomName", name)
        else
            armorStand.invokeMethod<Unit>(
                if (MinecraftVersion.majorLegacy >= 11800) "a" else "setCustomName",
                obcClass("util.CraftChatMessage").invokeMethod<Array<*>>("fromString", name, fixed = true)!![0]
            )
    }

    fun setCustomNameVisible(value: Boolean) {
        if (MinecraftVersion.majorLegacy >= 11800) {
            armorStand.invokeMethod<Unit>("n", value)
        } else
            armorStand.invokeMethod<Unit>("setCustomNameVisible", value)
    }

    fun delete() {
        isDeleted = true
        forViewers {
            visible(it, false)
        }
    }

    val viewers = Collections.synchronizedSet(HashSet<Player>())
    fun forViewers(consumer: Consumer<Player>) {
        viewers.forEach(consumer)
    }

    fun visible(viewer: Player, visible: Boolean) {
        if (visible) {
            viewers.add(viewer)
            spawn(viewer)
        } else {
            viewers.remove(viewer)
            destroy(viewer)
        }
    }

    fun spawn(viewer: Player) {
        val packetMeta = nmsClass("PacketPlayOutEntityMetadata").invokeConstructor(
            id,
            armorStand.invokeMethod<Any>(if (MinecraftVersion.majorLegacy >= 11800) "ai" else "getDataWatcher"),
            true
        )
        EntityUtils.spawnEntityLiving(viewer, id, normalizeUniqueId, location)
        viewer.sendPacket(packetMeta)
    }

    fun destroy(viewer: Player) {
        EntityUtils.destroyEntity(viewer, id)
    }

    fun destroy() {
        forViewers {
            destroy(it)
        }
    }

    fun respawn() {
        forViewers {
            spawn(it)
        }
    }

    fun teleport(location: Location) {
        this.location = location
        armorStand = nmsClass("EntityArmorStand").invokeConstructor(
            obcClass("CraftWorld").cast(location.world).invokeMethod("getHandle"),
            location.x,
            location.y,
            location.z
        )
        // 修改世界
        if (this.location.world!!.name != location.world?.name) {
            destroy()
            respawn()
        } else {
            forViewers { EntityUtils.teleportEntity(it, id, location) }
        }
    }
}