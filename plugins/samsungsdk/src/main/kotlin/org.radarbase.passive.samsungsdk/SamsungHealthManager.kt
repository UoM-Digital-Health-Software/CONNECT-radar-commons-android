package org.radarbase.passive.samsungsdk

import com.samsung.android.sdk.healthdata.*
import com.samsung.android.sdk.healthdata.HealthDataStore.ConnectionListener
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType
import org.radarbase.android.source.AbstractSourceManager
import org.radarbase.android.source.BaseSourceState
import org.slf4j.LoggerFactory
import java.lang.Boolean
import kotlin.Exception
import kotlin.String
import kotlin.TODO


class SamsungHealthManager(service: SamsungHealthService) :
    AbstractSourceManager<SamsungHealthService, BaseSourceState>(service) {
    override fun start(acceptableIds: Set<String>) {
        TODO("Not yet implemented")
    }

    //
    private var mPermissionListener : HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> =  HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {
        fun onResult( result : HealthPermissionManager.PermissionResult) {
        }
    };

    private val mConnectionListener: ConnectionListener = object : ConnectionListener {
        override fun onConnected() {
            logger.info("Health data service is connected.")
            val pmsManager = HealthPermissionManager(mStore)
            try {
                val resultMap = pmsManager.isPermissionAcquired(mKeySet)

                if (resultMap.containsValue(Boolean.FALSE)) {
                    // Request the permission for reading step counts if it is not acquired
                    pmsManager.requestPermissions(mKeySet, SamsungHealthPermissionsRationaleActivity()).setResultListener(mPermissionListener)
                } else {
                    // Get the current step count and display it
                    // ...
                }
            } catch (e: Exception) {
                logger.error("Permission setting fails.")
            }
        }

        override fun onConnectionFailed(error: HealthConnectionErrorResult) {
            logger.error("Health data service is not available.")
        }

        override fun onDisconnected() {
            logger.error("Health data service is disconnected.")
        }


    }

    private val logger = LoggerFactory.getLogger(SamsungHealthManager::class.java)

    private var  mKeySet : MutableSet<HealthPermissionManager.PermissionKey> = mutableSetOf<HealthPermissionManager.PermissionKey>();
    private var mStore: HealthDataStore? = null
    init {
        mStore = HealthDataStore(service, mConnectionListener)
        mKeySet.add(PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, PermissionType.READ))
        mStore?.connectService()
    }



}