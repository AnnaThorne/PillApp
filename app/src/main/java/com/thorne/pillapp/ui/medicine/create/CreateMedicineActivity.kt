package com.thorne.pillapp.ui.medicine.create

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.thorne.pillapp.ui.theme.PillAppTheme

class CreateMedicineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PillAppTheme {
                // A surface container using the 'background' color from the theme

            }
        }
    }
}

// need back button - main activity

// need to save medicine to storage

// need to load medicine from storage

// need to delete medicine from storage

// need to edit medicine from storage

