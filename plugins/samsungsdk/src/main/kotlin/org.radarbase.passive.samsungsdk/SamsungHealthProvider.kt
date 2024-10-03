package org.radarbase.passive.samsungsdk



import android.content.Context
import android.content.Intent
import kotlinx.coroutines.runBlocking
import org.radarbase.android.RadarService
import org.radarbase.android.source.BaseSourceState
import org.radarbase.android.source.SourceProvider
import org.radarbase.android.util.PermissionRequester
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants.StepCount;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType;
import org.slf4j.LoggerFactory


class SamsungHealthProvider(radarService: RadarService) : SourceProvider<BaseSourceState>(radarService) {
    private val logger = LoggerFactory.getLogger(SamsungHealthProvider::class.java)

    override val pluginNames: List<String> = listOf("samsung_health")
    override val serviceClass: Class<SamsungHealthService> = SamsungHealthService::class.java
    override val displayName: String
        get() = radarService.getString(R.string.google_health_connect_display)
    override val sourceProducer: String = "Samsung"
    override val sourceModel: String = "HealthAPI"
    override val version: String = "1.0.0"
    override val permissionsNeeded: List<String> = listOf()



init {
    logger.info("[SAMSUNGSDK]  create provider")

    val ac = SamsungHealthPermissionsRationaleActivity();
}



}