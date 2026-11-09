package org.radarbase.android

import android.app.Service
import android.content.Context
import android.os.Bundle
import org.radarbase.android.source.SourceService

interface RadarApplication {
    val authService: Class<out Service>

    val radarService: Class<out Service>
        get() = RadarService::class.java

    fun configureProvider(bundle: Bundle)
    fun onSourceServiceInvocation(service: SourceService<*>, bundle: Bundle, isNew: Boolean)
    fun onSourceServiceDestroy(service: SourceService<*>)

    companion object {
        val Context.radarApp: RadarApplication
            get() = applicationContext as RadarApplication

//
    }
}