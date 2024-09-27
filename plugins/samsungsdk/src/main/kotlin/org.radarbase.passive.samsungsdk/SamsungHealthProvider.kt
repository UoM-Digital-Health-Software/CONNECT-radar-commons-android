package org.radarbase.passive.samsungsdk



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



class SamsungHealthProvider(radarService: RadarService) : SourceProvider<BaseSourceState>(radarService) {

    override val pluginNames: List<String> = listOf("samsung_health")
    override val serviceClass: Class<SamsungHealthService> = SamsungHealthService::class.java
    override val displayName: String
        get() = radarService.getString(R.string.google_health_connect_display)
    override val sourceProducer: String = "Samsung"
    override val sourceModel: String = "HealthAPI"
    override val version: String = "1.0.0"
    override val permissionsNeeded: List<String> = listOf()








}