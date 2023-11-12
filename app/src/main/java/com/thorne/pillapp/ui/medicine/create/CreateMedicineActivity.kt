package com.thorne.pillapp.ui.medicine.create

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.thorne.pillapp.MainActivity
import com.thorne.pillapp.R
import com.thorne.pillapp.ui.theme.PillAppTheme
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
                Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxHeight()) {
                    CreateMedicineScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMedicineScreen() {
    var medicineName by rememberSaveable { mutableStateOf("") }
    var medicineDosage by rememberSaveable { mutableStateOf("") }
    var medicineHourlyFrequency by rememberSaveable { mutableStateOf("1") }
    var medicineNotes by rememberSaveable { mutableStateOf("") }
    var medicineStartDate by rememberSaveable { mutableStateOf(Date().time) }
    var medicineEndDate by rememberSaveable { mutableStateOf(Date().time) }

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

        Spacer(modifier = Modifier.padding(8.dp))

        CreateStartDateSelection { medicineStartDate = it}

        Spacer(modifier = Modifier.padding(8.dp))

        CreateEndDateSelection { medicineEndDate = it}

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
                    endDate = medicineEndDate,
                    startDate = medicineStartDate,
                    onInvalidate = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.value_is_empty, context.getString(it)),
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onValidate = {
                        // DONE! Navigate to next screen / Store medication info
                        saveMedication(medicineName, medicineDosage, medicineHourlyFrequency,  medicineStartDate, medicineEndDate, medicineNotes)
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
    endDate: Long,
    startDate: Long,
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

    if (endDate < 1) {
        onInvalidate(R.string.medicine_end_date)
        return
    }

    onValidate()
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

    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val datePickerDialog =
        DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, dayOfMonth)
            selectedDate = "${month.toMonthName()} $dayOfMonth, $year"
            endDate(newDate.timeInMillis)
        }, year, month, day)

    TextField(
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        value = selectedDate,
        onValueChange = {},
        trailingIcon = { Icons.Default.DateRange },
        interactionSource = interactionSource
    )

    if (isPressed) {
        datePickerDialog.show()
    }
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

    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val datePickerDialog =
        DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, dayOfMonth)
            selectedDate = "${month.toMonthName()} $dayOfMonth, $year"
            startDate(newDate.timeInMillis)
        }, year, month, day)

    TextField(
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        value = selectedDate,
        onValueChange = {},
        trailingIcon = { Icons.Default.DateRange },
        interactionSource = interactionSource
    )

    if (isPressed) {
        datePickerDialog.show()
    }
}

fun saveMedication(name: String, dosage: String, frequency: String,  startDate: Long, endDate: Long, notes: String){
    val med = MedicationImpl(name, dosage, frequency.toInt(), startDate, endDate, notes)
    MedSdkImpl.getInstance().addMedication(med)
    Log.d("Medication", MedSdkImpl.getInstance().getMedicationList().toString())
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
