package org.radarbase.passive.samsungsdk

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity
import com.samsung.android.sdk.healthdata.*
import com.samsung.android.sdk.healthdata.HealthDataStore.ConnectionListener
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType
import org.radarbase.android.source.AbstractSourceManager
import org.radarbase.android.source.BaseSourceState
import org.radarbase.android.util.StartActivityForPermission
import org.slf4j.LoggerFactory
import java.lang.Boolean
import kotlin.Exception
import kotlin.String



class SamsungHealthManager(service: SamsungHealthService) :
    AbstractSourceManager<SamsungHealthService, BaseSourceState>(service) {
    override fun start(acceptableIds: Set<String>) {
        logger.info("[SAMSUNGSDK] Samsung Health data is connected and starting.")


    }

    //
    private var mPermissionListener : HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> =  HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {
        fun onResult( result : HealthPermissionManager.PermissionResult) {
        }
    };

    private val mConnectionListener: ConnectionListener = object : ConnectionListener {
        override fun onConnected() {
            logger.info("[SAMSUNGSDK] Health data service is connected.")
            val pmsManager = HealthPermissionManager(mStore)
            try {
                logger.info("[SAMSUNGSDK] before isPermissionAcquired")
                val resultMap = pmsManager.isPermissionAcquired(mKeySet)
                logger.info("[SAMSUNGSDK] after isPermissionAcquired")
                if (resultMap.containsValue(Boolean.FALSE)) {
                    // Request the permission for reading step counts if it is not acquired
                    logger.info("[SAMSUNGSDK] before requestPermissions")


                    logger.info("[SAMSUNGSDK] after activity creation")




              //      res.

          //          val packageName = service.applicationContext.packageName
           //         val i = service.packageManager.getLaunchIntentForPackage(packageName)



        //            val i = //Intent(this, SamsungHealthPermissionsRationaleActivity::class.java)
//                    i.action = Intent.ACTION_MAIN
//                    i.addCategory(Intent.CATEGORY_LAUNCHER)
//                    startActivity(i)
                       pmsManager.requestPermissions(mKeySet ).setResultListener(mPermissionListener)




                 //       pmsManager.requestPermissions(mKeySet, ).setResultListener(mPermissionListener)






                } else {
                    // Get the current step count and display it
                    // ...
                }
            } catch (e: Exception) {
                logger.error("[SAMSUNGSDK] Permission setting fails. {}", e)
            }
        }

        override fun onConnectionFailed(error: HealthConnectionErrorResult) {
            logger.error("[SAMSUNGSDK] Health data service is not available.")
        }

        override fun onDisconnected() {
            logger.error("[SAMSUNGSDK] Health data service is disconnected.")
        }


    }

    private val logger = LoggerFactory.getLogger(SamsungHealthManager::class.java)

    private var  mKeySet : MutableSet<HealthPermissionManager.PermissionKey> = mutableSetOf<HealthPermissionManager.PermissionKey>();
    private var mStore: HealthDataStore? = null



    init {
        logger.info("[SAMSUNGSDK] Initialising")



        logger.info("[SAMSUNGSDK] after activity creation")
        mStore = HealthDataStore(service, mConnectionListener)
        mKeySet.add(PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, PermissionType.READ))
        mStore?.connectService()
    }



}