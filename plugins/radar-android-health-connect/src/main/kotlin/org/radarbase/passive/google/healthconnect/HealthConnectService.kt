package org.radarbase.passive.google.healthconnect

import android.os.Build
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import org.radarbase.android.config.SingleRadarConfiguration
import org.radarbase.android.source.BaseSourceState
import org.radarbase.android.source.SourceManager
import org.radarbase.android.source.SourceService
import org.radarbase.android.source.UnavailableSourceManager
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

@RequiresApi(Build.VERSION_CODES.O_MR1)
class HealthConnectService : SourceService<BaseSourceState>() {
    override val defaultState: BaseSourceState
        get() = BaseSourceState()

    override fun createSourceManager(): SourceManager<BaseSourceState> {
        return if (isHealthConnectAvailable()) {
            HealthConnectManager(this)
        } else {
            UnavailableSourceManager("Health Connect", state)
        }
    }


    @CallSuper
    override fun onDestroy() {
    if( sourceManager != null &&  sourceManager is HealthConnectManager) {
        try {
            var manager = sourceManager as HealthConnectManager
            manager.processor.close();
        } catch (e: Exception) {
            logger.error("Error on destroy {}", e.message)
        }
    }
        super.onDestroy()
    }

    override fun configureSourceManager(
        manager: SourceManager<BaseSourceState>,
        config: SingleRadarConfiguration
    ) {
        if (
            manager !is HealthConnectManager ||
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1
        ) return
        config.optString(HEALTH_CONNECT_DATA_TYPES) {
            manager.dataTypes = it.toHealthConnectTypes()
                .toHashSet()
        }
        config.optDouble(HEALTH_CONNECT_INTERVAL_SECONDS) {
            manager.interval = it.seconds
        }
    }

    companion object {
        const val HEALTH_CONNECT_DATA_TYPES = "health_connect_data_types"
        const val HEALTH_CONNECT_INTERVAL_SECONDS = "health_connect_interval_seconds"
        private val logger = LoggerFactory.getLogger(HealthConnectService::class.java)
    }
}
