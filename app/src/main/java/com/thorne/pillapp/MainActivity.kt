package com.thorne.pillapp

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thorne.pillapp.ui.medicine.create.CreateMedicineActivity
import com.thorne.pillapp.ui.theme.PillAppTheme
import com.thorne.sdk.MedSdkImpl
import com.thorne.sdk.meds.Medication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SDK with application context
        initSdk(this.applicationContext)

        setContent {
            PillAppTheme {
                // A surface container using the 'background' color from the theme
                PillApp()
            }
        }
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        Log.i("MainActivity", "onActivityReenter")
        setContent {
            PillAppTheme {
                // A surface container using the 'background' color from the theme
                PillApp()
            }
        }
    }

}

fun startCreateMedicineActivity(context: Context) {
    val intent = Intent(context, CreateMedicineActivity::class.java)
    context.startActivity(intent)

}


@Composable
private fun PillApp(modifier: Modifier = Modifier) {

    // TODO: make this persistent across app restarts
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

    Surface(modifier, color = colorScheme.background) {
        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        } else {
            PillCards(modifier = modifier, meds = MedSdkImpl.getInstance().getMedicationList())
            val context = LocalContext.current
            AddMedicationButton {
                startCreateMedicineActivity(context)
            }
        }
    }
}

@Composable
private fun PillCard(med: Medication) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primary
        ),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        CardContent(med)
    }
}


//TODO Add a edit button and stuff
@Composable
private fun CardContent(med: Medication) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(text = SimpleDateFormat("dd/MM/yy",Locale.getDefault()).format(Date(med.getStartDate())) + " - " + SimpleDateFormat("dd/MM/yy",Locale.getDefault()).format(Date(med.getEndDate())))
            // Text(text = med.getHourlyFrequency().toString() + " times a day")
            Text(
                text = med.getName(), style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (expanded) {
                Text(
                    text = med.getNotes()
                )
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}


@Composable
private fun PillCards(
    modifier: Modifier = Modifier, meds: List<Medication> = listOf()
) {
    Surface(
        modifier = modifier, color = colorScheme.background
    ) {
        LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
            items(items = meds) { med ->
                PillCard(med = med)
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to PillApp!")
        Button(
            modifier = Modifier.padding(vertical = 24.dp), onClick = onContinueClicked
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun AddMedicationButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {

        FloatingActionButton(
            onClick = { onClick() },
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.secondary,
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
    }
}

private fun initSdk(context: Context) {
    val medSdk = MedSdkImpl.getInstance()
    try {
        medSdk.initialize(context)
    } catch (e: Exception) {
        Log.e("MainActivity", "Error during SDK initialization: ${e.message}")
        e.printStackTrace()
    }
    Log.i("MainActivity", "SDK ver: ${medSdk.getSdkVersion()}")
}

/////////////////////////////
// Previews
/////////////////////////////
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun GreetingsPreview() {
    PillAppTheme {
        PillCards()
    }
}

@Preview
@Composable
fun MyAppPreview() {
    PillAppTheme {
        PillApp(Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    PillAppTheme {
        OnboardingScreen(onContinueClicked = {}) // Do nothing on click
    }
}

// Dark Mode Preview
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    PillAppTheme {
        PillCards()
        AddMedicationButton { /*Empty for Preview*/ }
    }
}

