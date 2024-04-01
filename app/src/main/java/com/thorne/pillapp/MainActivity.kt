package com.thorne.pillapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.rounded.ArrowDownward
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.thorne.pillapp.ui.dialogues.ConfirmDialog
import com.thorne.pillapp.ui.medicine.create.CreateMedicineActivity
import com.thorne.pillapp.ui.medicine.edit.EditMedicineActivity
import com.thorne.pillapp.ui.theme.PillAppTheme
import com.thorne.sdk.MedSdkImpl
import com.thorne.sdk.meds.Medication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {

    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get shared preferences
        prefs = getSharedPreferences("com.thorne.pillapp", MODE_PRIVATE)

        // Initialize SDK with application context
        initSdk(this.applicationContext)

        // Check app permissions
        checkPermissions()

        // Init notification channel
        val notificationChannel = NotificationChannel(
            getString(R.string.reminders_notification_channel_id),
            "Med reminder",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        setContent {
            PillAppTheme {
                PillApp()
            }
        }
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        Log.i("MainActivity", "onActivityReenter")
        setContent {
            PillAppTheme {
                PillApp()
            }
        }
    }

    @Composable
    private fun PillApp(modifier: Modifier = Modifier) {
        // Variables to hold the state of the onboarding
        // and the list of medications
        var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
        val meds = MedSdkImpl.getInstance().getMedicationList()

        // Surface container using the 'background' color from the theme
        Surface(modifier, color = colorScheme.background) {

            // Check if this is the first run
            if (prefs!!.getBoolean("first_run", true)) {
                shouldShowOnboarding = true
                prefs!!.edit().putBoolean("first_run", false).apply()
            } else {
                shouldShowOnboarding = false
            }

            // Show onboarding screen
            if (shouldShowOnboarding) {
                OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
            } else {

                // Show list of medications
                PillCards(modifier = modifier, meds = meds)

                // Show hint to add medication if list is empty
                if (meds.isEmpty()) {
                    showHintForAddingMeds()
                }

                // Add medication button
                val context = LocalContext.current
                AddMedicationButton {
                    startCreateMedicineActivity(context)
                }
            }
        }
    }

    // Medication card
    @Composable
    private fun PillCard(med: Medication, onDeleteClick: (Medication) -> Unit) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.primary
            ), modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            CardContent(med, onDeleteClick, LocalContext.current)
        }
    }

    private fun startCreateMedicineActivity(context: Context) {
        val intent = Intent(context, CreateMedicineActivity::class.java)
        context.startActivity(intent)
    }

    private fun startEditMedicineActivity(context: Context, med: Medication) {
        val intent = Intent(context, EditMedicineActivity::class.java)
        intent.putExtra("medId", med.getId())
        intent.putExtra("medName", med.getName())
        intent.putExtra("medDosage", med.getDosage())
        intent.putExtra("medFrequency", med.getFrequency())
        intent.putExtra("medStartHour", med.getStartHour())
        intent.putExtra("medStartMin", med.getStartMin())
        intent.putExtra("medStartDate", med.getStartDate())
        intent.putExtra("medEndDate", med.getEndDate())
        intent.putExtra("medNotes", med.getNotes())
        context.startActivity(intent)
    }

    // Medication card content
    @Composable
    private fun CardContent(
        med: Medication, onDeleteClick: (Medication) -> Unit, context: Context
    ) {
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
                Row {
                    Text(
                        text = SimpleDateFormat(
                            "dd/MM/yy", Locale.getDefault()
                        ).format(Date(med.getStartDate())) + " - " + SimpleDateFormat(
                            "dd/MM/yy", Locale.getDefault()
                        ).format(Date(med.getEndDate())),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Row {
                    Text(
                        text = med.getName(), style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
                Row {
                    Text(
                        text = med.getDosage(), style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                if (expanded) {
                    Row {
                        Text(
                            text = getString(R.string.medicine_start_time) + ":",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = med.getStartHour().toString() + ":" + med.getStartMin()
                                .toString(),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )

                    }

                    Row {
                        Text(
                            text = getString(R.string.medicine_frequency) + ":",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = med.getFrequency().toString(),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )

                    }

                    Row {
                        Text(
                            text = getString(R.string.medicine_notes) + ":",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = med.getNotes(), style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            ), textAlign = TextAlign.Center
                        )

                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Expand button
                Row {
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
                if (expanded) {
                    // Edit button
                    Row {
                        IconButton(onClick = {
                            startEditMedicineActivity(context, med)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit, contentDescription = "Edit"
                            )
                        }
                    }

                    // Delete button
                    Row {
                        val openConfirmDialog = remember { mutableStateOf(false) }

                        val cd = ConfirmDialog()
                        if (openConfirmDialog.value) {
                            cd.ShowConfirmationDialog(
                                onDismissRequest = { openConfirmDialog.value = false },
                                onConfirmation = {
                                    onDeleteClick(med)
                                    openConfirmDialog.value = false
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.success),
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                dialogTitle = stringResource(R.string.dialog_deletion_title),
                                dialogText = stringResource(R.string.dialog_deletion_text),
                                icon = Icons.Default.DeleteForever
                            )
                        }
                        IconButton(onClick = {
                            openConfirmDialog.value = true

                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete, contentDescription = "Delete"
                            )
                        }
                    }
                }
            }
        }
    }

    // List of medications
    @Composable
    private fun PillCards(
        modifier: Modifier = Modifier, meds: List<Medication> = emptyList()
    ) {
        val medications = remember { mutableStateListOf<Medication>() }
        medications.addAll(meds)
        Surface(
            modifier = modifier, color = colorScheme.background
        ) {
            LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
                items(items = medications) { med ->
                    PillCard(med = med, onDeleteClick = { med ->
                        MedSdkImpl.getInstance().removeMedication(med)
                        medications.remove(med)
                    })

                }
            }
        }
    }

    // Onboarding screen
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

    // Add medication button
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
                    .size(60.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    "Add medicine button",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }

    // Initialize SDK
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

    // Check permissions
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.SCHEDULE_EXACT_ALARM,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                    1
                )
            }
        }
    }

    @Composable
    private fun showHintForAddingMeds() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.add_medication_hint),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
                Icon(
                    Icons.Rounded.ArrowDownward,
                    contentDescription = stringResource(id = R.string.arrow_down_content_description),
                )
            }
        }
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
}