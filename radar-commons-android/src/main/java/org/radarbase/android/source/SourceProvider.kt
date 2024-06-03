/*
 * Copyright 2017 The Hyve
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.radarbase.android.source

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.Keep
import org.radarbase.android.RadarService
import org.radarbase.android.auth.AppAuthState
import org.radarbase.android.auth.SourceType
import org.radarbase.android.util.BluetoothStateReceiver.Companion.bluetoothPermissionList
import org.radarbase.android.util.PermissionRequester
import org.slf4j.LoggerFactory

/**
 * RADAR service provider, to bind and configure to a service. It is not thread-safe.
 * @param <T> state that the Service will provide.
 */
@Keep
abstract class SourceProvider<T : BaseSourceState>(protected val radarService: RadarService) {
    private var _connection: SourceServiceConnection<T>? = null
   var intent : Intent? = null;

    val connection: SourceServiceConnection<T>
        get() = _connection
                ?: SourceServiceConnection<T>(radarService, serviceClass.name)
                        .also { _connection = it }

    /**
     * Whether [.bind] has been called and [.unbind] has not been called since then.
     * @return true if bound, false otherwise
     */
    var isBound: Boolean = false
        private set

    /**
     * Names that the service can be identified with. The first one is used as an identifier.
     */
    abstract val pluginNames: List<String>

    val pluginName: String
        get() = pluginNames.first()

    /**
     * Class of the service.
     */
    abstract val serviceClass: Class<out SourceService<*>>

    /** Display name of the service.  */
    abstract val displayName: String

    /**
     * Image to display when onboarding for this service.
     * @return resource number or -1 if none is available.
     */
    open val descriptionImage: Int
        get() = -1

    /**
     * Description of the service. This should tell what the service does and why certain
     * permissions are needed.
     * @return description or `null` if no description is needed.
     */
    open val description: String?
        get() = null

    abstract val sourceProducer: String

    abstract val sourceModel: String

    private val appSourceModel: String
        get() = sourceModel

    abstract val version: String

    /**
     * Whether the service has a UI detail view that can be invoked. If not,
     * [.showDetailView] will throw an UnsupportedOperationException.
     */
    open val hasDetailView: Boolean
        get() = false

    /**
     * Show a detail view from the MainActivity.
     * @throws UnsupportedOperationException if [.hasDetailView] is false.
     */
    fun showDetailView() {
        throw UnsupportedOperationException()
    }

    /**
     * Bind the service called.
     */
    fun bind() {
        check(!isBound) { "Service is already bound" }
        try {
            logger.debug("Binding {}", this)
            val extras = Bundle()
            configure(extras)

            val intent = Intent(radarService, serviceClass).apply {
                putExtras(extras)
            }

            radarService.startService(intent)
            logger.warn("Binding above client")
            radarService.bindService(intent, connection, Context.BIND_ABOVE_CLIENT)

            this.intent = intent

            isBound = true
        } catch (ex: IllegalStateException) {
            logger.warn("App is in background. Cannot bind to any further providers.")
        }

    }

    /**
     * Unbind the service.
     */
    fun unbind() {
        check(isBound) { "Service is not bound" }
        logger.debug("Unbinding {}", this)
        isBound = false
        radarService.unbindService(connection)

        connection.onServiceDisconnected(null)

        if(this.intent != null) {
            try {
                radarService.stopService(intent)
            }
            catch(e: Exception){
                logger.error("Error stopping service in SourceProvider: {}", e.message)
            }
        }
    }

    /**
     * Update the configuration of the service based on the given RadarConfiguration.
     * @throws IllegalStateException if [.getConnection] has not been called
     * yet.
     */
    fun updateConfiguration() {
        if (connection.hasService()) {
            val bundle = Bundle()
            configure(bundle)
            connection.updateConfiguration(bundle)
        }
    }

    /**
     * Configure the service from the set RadarConfiguration.
     */
    @CallSuper
    protected fun configure(bundle: Bundle) {
        // Add the default configuration parameters given to the service intents
        val permissions = permissionsNeeded
        bundle.apply {
            putBoolean(NEEDS_BLUETOOTH_KEY, bluetoothPermissionList.any { p ->
                "BLUETOOTH" in p && p in permissions
            })
            putString(PLUGIN_NAME_KEY, pluginName)
            putString(PRODUCER_KEY, sourceProducer)
            putString(MODEL_KEY, appSourceModel)
        }
    }

    /** Whether [.getConnection] has already been called.  */
    val isConnected: Boolean
        get() = _connection != null

    override fun toString(): String = pluginName

    open val mayBeConnectedInBackground: Boolean = true

    /**
     * Android permissions that the underlying service needs to function correctly.
     */
    abstract val permissionsNeeded: List<String>


    /**
     * Android permissions that the underlying service can take advantage of.
     * If they are not granted, the plugin can still run.
     */
    open val permissionsRequested: List<String>
        get() = listOf()

    open val requestPermissionResultContract: List<PermissionRequester> = listOf()

    /**
     * Android features (Under PackageManager.FEATURE_) that the provider requires. If the feature
     * is not available, the provider will not be enabled.
     */
    open val featuresNeeded: List<String> = emptyList()

    /** Whether the current service can meaningfully be displayed.  */
    open val isDisplayable: Boolean = true

    /**
     * Whether the source name should be checked with given filters before a connection is allowed
     */
    open val isFilterable: Boolean = false

    /**
     * Match source type.
     *
     * @param sourceType source type
     * @param checkVersion whether to do a strict version check
     */
    fun matches(sourceType: SourceType?, checkVersion: Boolean): Boolean =
        sourceType != null &&
        sourceType.producer.equals(sourceProducer, ignoreCase = true) &&
        sourceType.model.equals(appSourceModel, ignoreCase = true) &&
        (!checkVersion || sourceType.catalogVersion.equals(version, ignoreCase = true))

    /**
     * Whether given provider matches any registered source or source type. If no registration is
     * required, this always returns true. A source with matching type is always considered a match.
     * If no source is registered but registration is dynamic, a match is also made.
     */
    fun canRegisterFor(state: AppAuthState, checkVersion: Boolean): Boolean =
        !state.needsRegisteredSources ||
        state.sourceMetadata.any { matches(it.type, checkVersion) } ||
        state.sourceTypes.any { it.hasDynamicRegistration && matches(it, checkVersion) }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other.javaClass == javaClass
    }

    fun isRegisteredFor(authState: AppAuthState, checkVersion: Boolean): Boolean =
        !authState.needsRegisteredSources ||
        authState.sourceMetadata.any {
            matches(it.type, checkVersion) && authState.isAuthorizedForSource(it.sourceId)
        }

    companion object {
        const val NEEDS_BLUETOOTH_KEY = "org.radarbase.android.source.SourceProvider.needsBluetooth"
        const val PLUGIN_NAME_KEY = "org.radarbase.android.source.SourceProvider.pluginName"
        const val PRODUCER_KEY = "org.radarbase.android.source.SourceProvider.sourceProducer"
        const val MODEL_KEY = "org.radarbase.android.source.SourceProvider.sourceModel"

        private val logger = LoggerFactory.getLogger(SourceProvider::class.java)

    }

}
