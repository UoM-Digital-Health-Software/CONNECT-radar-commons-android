package org.radarbase.android.config

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicBoolean

class LocalConfiguration(context: Context) : LocalConfig {
    override val hasChanges: Boolean
        get() = hasChange.get()

    private val hasChange: AtomicBoolean = AtomicBoolean(false)
    private val preferences: SharedPreferences = context.getSharedPreferences("org.radarbase.android.config.LocalConfiguration", MODE_PRIVATE)

    override val config: ConcurrentMap<String, String> = ConcurrentHashMap<String, String>().apply {
        preferences.all.forEach { (k, v) ->
            if (v is String) {
                put(k, v)
            }
        }
    }

    override fun put(key: String, value: Any): String? {
        val stringValue: String = if (value is String) {
            value
        } else {
            require(
                    value is Long ||
                        value is Int ||
                        value is Float ||
                        value is Boolean
            ) { "Cannot put value of type ${value.javaClass} into RadarConfiguration" }
            value.toString()
        }
        val oldValue = config[key]
        if (stringValue.isNotEmpty()) {
            if (oldValue != stringValue) {
                hasChange.set(true)
            }
            config[key] = stringValue
        }
        return oldValue
    }

    override fun persistChanges(): Boolean {
        return if (hasChange.compareAndSet(true, false)) {
            val editor = preferences.edit()
            config.forEach { (key, value) ->
                editor.putString(key, value)
            }
            editor.apply()
            true
        } else {
            false
        }
    }

    override fun removeAll(keys: Array<out String>) {
        if (keys.isEmpty()) {
            if (config.isNotEmpty()) {
                config.clear()
                hasChange.set(true)
            }
        } else if (config.keys.removeAll(keys.toHashSet())) {
            hasChange.set(true)
        }
    }

    override operator fun get(key: String): String? = config[key]

    override operator fun contains(key: String): Boolean = key in config

    override val keys: Set<String> = config.keys

    override fun toMap(): Map<String, String> = HashMap(config)

    override fun toString(): String {
        return "LocalConfiguration(config=$config)"
    }
}
