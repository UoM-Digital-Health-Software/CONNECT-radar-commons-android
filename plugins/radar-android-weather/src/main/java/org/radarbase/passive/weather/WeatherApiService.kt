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

package org.radarbase.passive.weather

import okhttp3.OkHttpClient
import org.radarbase.android.config.SingleRadarConfiguration
import org.radarbase.android.source.BaseSourceState
import org.radarbase.android.source.SourceManager
import org.radarbase.android.source.SourceService
import org.radarbase.config.ServerConfig
import org.radarbase.passive.weather.WeatherApiManager.Companion.SOURCE_OPENWEATHERMAP
import org.radarbase.producer.rest.RestClient
import java.util.concurrent.TimeUnit

class WeatherApiService : SourceService<BaseSourceState>() {
    private lateinit var client: OkHttpClient

    override val defaultState: BaseSourceState
        get() = BaseSourceState()

    override fun onCreate() {
        super.onCreate()
        client = RestClient.global()
                .server(ServerConfig())
                .build()
                .httpClient  // global OkHttpClient
    }

    override fun createSourceManager() = WeatherApiManager(this, client)

    override fun configureSourceManager(manager: SourceManager<BaseSourceState>, config: SingleRadarConfiguration) {
        manager as WeatherApiManager
        manager.setQueryInterval(config.getLong(WEATHER_QUERY_INTERVAL, WEATHER_QUERY_INTERVAL_DEFAULT), TimeUnit.SECONDS)
        manager.setSource(
            config.getString(WEATHER_API_SOURCE, WEATHER_API_SOURCE_DEFAULT),
            config.optString(WEATHER_API_KEY),
        )
        config.optDouble(WEATHER_RANDOM_RADIUS) {
            manager.randomRadius = it
        }
    }

    companion object {
        private const val WEATHER_QUERY_INTERVAL = "weather_query_interval_seconds"
        private const val WEATHER_API_SOURCE = "weather_api_source"
        private const val WEATHER_API_KEY = "weather_api_key"
        private const val WEATHER_RANDOM_RADIUS = "weather_random_radius_meters"


        internal val WEATHER_QUERY_INTERVAL_DEFAULT = TimeUnit.HOURS.toSeconds(3)
        internal const val WEATHER_API_SOURCE_DEFAULT = SOURCE_OPENWEATHERMAP
    }
}
