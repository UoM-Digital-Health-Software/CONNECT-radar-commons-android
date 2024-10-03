package org.radarbase.passive.samsungsdk

import androidx.annotation.CallSuper
import org.radarbase.android.config.SingleRadarConfiguration
import org.radarbase.android.source.BaseSourceState
import org.radarbase.android.source.SourceManager
import org.radarbase.android.source.SourceService
import org.slf4j.LoggerFactory


class SamsungHealthService : SourceService<BaseSourceState>() {
    override val defaultState: BaseSourceState
        get() = BaseSourceState()

    override fun createSourceManager(): SourceManager<BaseSourceState> {

       return SamsungHealthManager(this);

    }


    @CallSuper
    override fun onDestroy() {
        if( sourceManager != null &&  sourceManager is SamsungHealthManager) {
            try {
                var manager = sourceManager as SamsungHealthManager
                manager.close()
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
//        if (
//            manager !is HealthConnectManager ||
//            Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1
//        ) return
//        config.optString(HEALTH_CONNECT_DATA_TYPES) {
//            manager.dataTypes = it.toHealthConnectTypes()
//                .toHashSet()
//        }
//        config.optDouble(HEALTH_CONNECT_INTERVAL_SECONDS) {
//            manager.interval = it.seconds
//        }
    }

    companion object {
        const val HEALTH_CONNECT_DATA_TYPES = "health_connect_data_types"
        const val HEALTH_CONNECT_INTERVAL_SECONDS = "health_connect_interval_seconds"
        private val logger = LoggerFactory.getLogger(SamsungHealthService::class.java)
    }

}