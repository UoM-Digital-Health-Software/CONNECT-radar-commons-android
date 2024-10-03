package org.radarbase.passive.samsungsdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity

import org.radarbase.passive.samsungsdk.ui.Rationale
import org.radarbase.passive.samsungsdk.ui.theme.RadarCommonsTheme
import org.slf4j.LoggerFactory

class SamsungHealthPermissionsRationaleActivity : AppCompatActivity() {

    private val logger = LoggerFactory.getLogger(SamsungHealthPermissionsRationaleActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logger.info("[SAMSUNGSDK] creating activity")

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