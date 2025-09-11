package org.radarbase.passive.google.healthconnect.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.radarbase.passive.google.healthconnect.R
import org.radarbase.passive.google.healthconnect.ui.theme.RadarCommonsTheme


@Composable
fun Rationale(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val participantInfoUrl =
        "http://drive.google.com/viewerng/viewer?embedded=true&url=https://documents.manchester.ac.uk/display.aspx?DocID=37095"

    val annotatedText = buildAnnotatedString {
        append("This policy explains how your data is collected and shared by the CONNECT app. It should be read in conjunction with the Participant Information Sheet.\n\n")

        append("The CONNECT app collects data from your phone and wearable device’s sensors (passive data, e.g. physical activity) and data that you enter (active data, e.g. questions on mood). This data is collected for the purposes of promoting users’ health. All data is handled in accordance with the ")

        // 🔗 University privacy policy link
        pushStringAnnotation(
            tag = "URL",
            annotation = participantInfoUrl
        )
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("University of Manchester privacy policy for research participants")
        }
        pop()
        append(".\n\n")

        append("All data is labelled with your study ID number (pseudo-anonymised), encrypted, and transferred to the research server. The research server is hosted by Amazon Web Services, physically located in London, and is managed by a University of Manchester-approved company. Like the other pseudo-anonymised data we use in CONNECT and in line with research data storage best practice, this data will be stored for 20 years. Access to your pseudo-anonymised data is restricted to authorised members of the research team and controlled by secure login. Your data is not shared with anyone else and are not shared for any purpose other than management of the infrastructure that underpins the research database that is required for data analysis. The use of information received from Health Connect will adhere to the ")

        // 🔗 Health Connect policy link
        pushStringAnnotation(
            tag = "URL",
            annotation = "https://support.google.com/googleplay/android-developer/answer/9888170?sjid=8848401409772856259-EU#ahp" // Replace with real Health Connect permissions URL
        )
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Health Connect Permissions Policy")
        }
        pop()
        append(", including the Limited Use requirements.\n\n")

        append("The CONNECT app accesses the raw data from your phone and watch’s sensors. This data forms the passive data we use in the CONNECT study. Collection of this data is optional.\n\n")

        append("In order to collect the passive data we use in the study, we access the following data:\n\n")
        append("• Pedometer\n")
        append("• Elevation\n")
        append("• Location (relative to a fixed point, to calculate how far you move in a day)\n")
        append("• How you use your phone (including number of calls made and received, and how many different people you are in contact with, but not the content of the calls themselves or the phone numbers of the people you are in contact with)\n")
        append("• How you use the keyboard on your phone\n")
        append("• Light levels\n")
        append("• How often you use your phone and your usage patterns, such as what types of app you use and how often you use them, and how often you charge your phone\n")
        append("• How quickly you move around\n")
        append("• Exercises, heart rate, steps, and sleep stages through Health Connect\n\n")

        append("If you choose to withdraw from the study, the health data collected by the CONNECT app can be deleted at your request if it has not yet been anonymised and fed into the machine-learning algorithm. After anonymisation, it is impossible to trace from whom the data originated, and it is therefore not possible to delete it.")
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()), // scrollable
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.health_connect_permission_rationale_title),
            fontWeight = FontWeight.Bold
        )

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations("URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                        context.startActivity(intent)
                    }
            }
        )
    }
}