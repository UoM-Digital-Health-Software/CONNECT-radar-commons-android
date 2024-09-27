package org.radarbase.passive.samsungsdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import org.radarbase.passive.samsungsdk.ui.Rationale
import org.radarbase.passive.samsungsdk.ui.theme.RadarCommonsTheme

class SamsungHealthPermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RadarCommonsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Rationale()
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview() {
        RadarCommonsTheme {
            Rationale()
        }
    }

}