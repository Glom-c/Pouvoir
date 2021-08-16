package com.skillw.pouvoir.util

import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * ClassName : com.skillw.com.skillw.rpglib.util.MapUtils
 * Created by Glom_ on 2021-03-28 21:59:37
 * Copyright  2021 user. All rights reserved.
 */
object MapUtils {
    @JvmStatic
    fun <K, V> add(map: MutableMap<K, LinkedList<V>>, key: K, value: V): Map<K, LinkedList<V>> {
        if (!map.containsKey(key)) {
            map[key] = LinkedList(listOf(value))
        } else {
            map[key]!!.add(value)
        }
        return map
    }

    @JvmStatic
    fun <K, V, Z> put(map: MutableMap<K, MutableMap<Z, V>>, key1: K, key2: Z, value: V): Map<K, MutableMap<Z, V>> {
        if (!map.containsKey(key1)) {
            val map1: MutableMap<Z, V> = ConcurrentHashMap()
            map1[key2] = value
            map[key1] = map1
        } else {
            map[key1]!![key2] = value
        }
        return map
    }

    @JvmStatic
    fun <K, Z, V> getValues(map: Map<K, Map<Z, V>>): List<V> {
        val list: MutableList<V> = LinkedList()
        map.forEach { (k1: K, v1: Map<Z, V>) -> v1.forEach { (k2: Z, z: V) -> list.add(z) } }
        return list
    }
}