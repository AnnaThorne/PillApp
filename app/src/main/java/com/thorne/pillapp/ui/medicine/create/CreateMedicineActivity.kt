package com.thorne.pillapp.ui.medicine.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.thorne.pillapp.MainActivity
import com.thorne.pillapp.R
import com.thorne.pillapp.ui.theme.PillAppTheme
import com.thorne.pillapp.util.reminders.MedNotificationService
import com.thorne.sdk.MedSdkImpl
import com.thorne.sdk.meds.MedicationImpl
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateMedicineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PillAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    CreateMedicineScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CreateMedicineScreen() {
        var medicineName by rememberSaveable { mutableStateOf("") }
        var medicineDosage by rememberSaveable { mutableStateOf("") }
        var medicineHourlyFrequency by rememberSaveable { mutableStateOf("") }
        var medicineStartHour by rememberSaveable { mutableIntStateOf(0) }
        var medicineStartMin by rememberSaveable { mutableIntStateOf(0) }
        var medicineStartDate by rememberSaveable { mutableLongStateOf(Date().time) }
        var medicineEndDate by rememberSaveable { mutableLongStateOf(Date().time) }
        var medicineNotes by rememberSaveable { mutableStateOf("") }
        val timePickerState = rememberTimePickerState()

        val context = LocalContext.current

        Column(
            modifier = Modifier
                .padding(16.dp, 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = stringResource(id = R.string.create_medication),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = stringResource(id = R.string.medicine_name),
                style = MaterialTheme.typography.bodyLarge
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = medicineName,
                onValueChange = { medicineName = it },
                placeholder = { Text(text = "Name of the medication...") },
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.medicine_dosage),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    TextField(
                        modifier = Modifier.width(128.dp),
                        value = medicineDosage,
                        onValueChange = { medicineDosage = it },
                        placeholder = { Text(text = "e.g. 5 mg") },
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.medicine_frequency),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    TextField(
                        modifier = Modifier.width(128.dp),
                        value = medicineHourlyFrequency,
                        onValueChange = { medicineHourlyFrequency = it },
                        placeholder = { Text(text = "e.g. 3") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.padding(4.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.medicine_start_hour_and_minute),
                    style = MaterialTheme.typography.bodyLarge
                )
                TimeInput(
                    state = timePickerState,
                    modifier = Modifier.padding(8.dp)
                )
            }

            CreateStartDateSelection { medicineStartDate = it }

            Spacer(modifier = Modifier.padding(8.dp))

            CreateEndDateSelection { medicineEndDate = it }

            Spacer(modifier = Modifier.padding(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.medicine_notes),
                    style = MaterialTheme.typography.bodyLarge
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = medicineNotes,
                    onValueChange = { medicineNotes = it },
                    placeholder = { Text(text = "Notes...") },
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    validateMedication(
                        name = medicineName,
                        dosage = medicineDosage,
                        frequency = medicineHourlyFrequency,
                        startHour = timePickerState.hour,
                        startMinute = timePickerState.minute,
                        endDate = medicineEndDate,
                        startDate = medicineStartDate,
                        onInvalidate = {
                            Toast.makeText(
                                context,
                                context.getString(R.string.value_is_invalid, context.getString(it)),
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        onValidate = {
                            // Get values from time picker
                            medicineStartHour = timePickerState.hour
                            medicineStartMin = timePickerState.minute

                            // DONE! Navigate to next screen / Store medication info
                            saveMedication(
                                medicineName,
                                medicineDosage,
                                medicineHourlyFrequency,
                                medicineStartHour,
                                medicineStartMin,
                                medicineStartDate,
                                medicineEndDate,
                                medicineNotes,
                                context.applicationContext
                            )
                            Toast.makeText(
                                context,
                                context.getString(R.string.success),
                                Toast.LENGTH_LONG
                            ).show()
                            startOverviewActivity(context)
                        }
                    )
                },
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }


    private fun validateMedication(
        name: String,
        dosage: String,
        frequency: String,
        startHour: Int,
        startMinute: Int,
        startDate: Long,
        endDate: Long,
        onInvalidate: (Int) -> Unit,
        onValidate: () -> Unit
    ) {
        if (name.isEmpty()) {
            onInvalidate(R.string.medicine_name)
            return
        }

        if (dosage.isEmpty()) {
            onInvalidate(R.string.medicine_dosage)
            return
        }

        if (frequency.isEmpty()) {
            onInvalidate(R.string.medicine_name)
            return
        }

        if (startHour > 24 || startHour < 0) {
            onInvalidate(R.string.medicine_start_hour)
            return
        }

        if (startMinute > 60 || startMinute < 0) {
            onInvalidate(R.string.medicine_start_minute)
            return
        }

        if (startDate < 1) {
            onInvalidate(R.string.medicine_start_date)
            return
        }

        if (endDate < 1) {
            onInvalidate(R.string.medicine_end_date)
            return
        }

        if (startDate > endDate) {
            onInvalidate(R.string.medicine_end_date)
            return
        }
        onValidate()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CreateStartDateSelection(startDate: (Long) -> Unit) {

        Text(
            text = stringResource(id = R.string.medicine_start_date),
            style = MaterialTheme.typography.bodyLarge
        )

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed: Boolean by interactionSource.collectIsPressedAsState()

        val currentDate = Date().toFormattedString()
        var selectedDate by rememberSaveable { mutableStateOf(currentDate) }

        val calendar = Calendar.getInstance()
        var year: Int
        var month: Int
        var day: Int
        calendar.time = Date()

        var savedDate: Long

        val state = rememberDatePickerState()
        val openDialog = remember { mutableStateOf(false) }

        if (isPressed) {
            openDialog.value = true
        }
        if (openDialog.value) {
            DatePickerDialog(colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background,
                headlineContentColor = MaterialTheme.colorScheme.primary,
                weekdayContentColor = MaterialTheme.colorScheme.secondaryContainer
            ), onDismissRequest = {
                openDialog.value = false
            }, confirmButton = {
                TextButton(onClick = {
                    if (state.selectedDateMillis != null) {
                        startDate(state.selectedDateMillis!!)
                        savedDate = state.selectedDateMillis!!
                        calendar.time = Date(savedDate)
                        year = calendar.get(Calendar.YEAR)
                        month = calendar.get(Calendar.MONTH)
                        day = calendar.get(Calendar.DAY_OF_MONTH)
                        selectedDate = "${month.toMonthName()} $day, $year"
                        openDialog.value = false
                    }
                }) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                }) {
                    Text("CANCEL")
                }
            }) {
                DatePicker(
                    state = state
                )
            }
        }

        TextField(
            readOnly = true,
            value = selectedDate,
            onValueChange = {},
            trailingIcon = { Icons.Default.DateRange },
            interactionSource = interactionSource
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CreateEndDateSelection(endDate: (Long) -> Unit) {
        Text(
            text = stringResource(id = R.string.medicine_end_date),
            style = MaterialTheme.typography.bodyLarge
        )

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed: Boolean by interactionSource.collectIsPressedAsState()

        val currentDate = Date().toFormattedString()
        var selectedDate by rememberSaveable { mutableStateOf(currentDate) }


        val calendar = Calendar.getInstance()
        var year: Int
        var month: Int
        var day: Int
        calendar.time = Date()

        var savedDate: Long = 0

        val state = rememberDatePickerState()
        val openDialog = remember { mutableStateOf(false) }

        if (isPressed) {
            openDialog.value = true
        }
        if (openDialog.value) {
            DatePickerDialog(colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background,
                headlineContentColor = MaterialTheme.colorScheme.primary,
                weekdayContentColor = MaterialTheme.colorScheme.secondaryContainer
            ), onDismissRequest = {
                openDialog.value = false
            }, confirmButton = {
                TextButton(onClick = {
                    if (state.selectedDateMillis != null) {
                        endDate(state.selectedDateMillis!!)
                        savedDate = state.selectedDateMillis!!
                        calendar.time = Date(savedDate)
                        year = calendar.get(Calendar.YEAR)
                        month = calendar.get(Calendar.MONTH)
                        day = calendar.get(Calendar.DAY_OF_MONTH)
                        selectedDate = "${month.toMonthName()} $day, $year"
                        openDialog.value = false
                    }
                }) {
                    Text("OK")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                }) {
                    Text("CANCEL")
                }
            }) {
                DatePicker(
                    state = state
                )
            }
        }

        TextField(
            readOnly = true,
            value = selectedDate,
            onValueChange = {},
            trailingIcon = { Icons.Default.DateRange },
            interactionSource = interactionSource
        )
    }

    fun saveMedication(
        name: String,
        dosage: String,
        frequency: String,
        startHour: Int,
        startMinute: Int,
        startDate: Long,
        endDate: Long,
        notes: String,
        context: Context
    ) {
        val med = MedicationImpl(
            name,
            dosage,
            frequency.toInt(),
            startHour,
            startMinute,
            startDate,
            endDate,
            notes
        )
        MedSdkImpl.getInstance().addMedication(med)

        Log.d("Medication", MedSdkImpl.getInstance().getMedicationList().toString())

        MedNotificationService.startReminder(
            context,
            MedNotificationService.getNextId(),
            med.getId(),
            name,
            dosage,
            frequency.toInt(),
            startHour,
            startMinute,
            startDate,
            endDate
        )
    }

    fun startOverviewActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)

    }

    fun Int.toMonthName(): String {
        return DateFormatSymbols().months[this]
    }

    fun Date.toFormattedString(): String {
        val simpleDateFormat = SimpleDateFormat("LLLL dd, yyyy", Locale.getDefault())
        return simpleDateFormat.format(this)
    }
}
