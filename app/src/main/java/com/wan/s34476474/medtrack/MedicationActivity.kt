package com.wan.s34476474.medtrack

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wan.s34476474.medtrack.data.medications.Medication
import com.wan.s34476474.medtrack.data.medications.MedicationsViewModel
import com.wan.s34476474.medtrack.data.patients.PatientsViewModel
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme
import com.wan.s34476474.medtrack.utils.timePickerFun
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.String

class MedicationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val medicationViewModel: MedicationsViewModel = ViewModelProvider(
            this, MedicationsViewModel.MedicationsViewModelFactory(application)
        )[MedicationsViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MedtrackTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    MedicationAddingLayout(
                        innerPadding = innerPadding,
                        snackbarHostState = snackbarHostState,
                        medicationsViewModel = medicationViewModel
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationAddingLayout(
    innerPadding: PaddingValues, snackbarHostState: SnackbarHostState,
    medicationsViewModel: MedicationsViewModel
) {
    val context = LocalContext.current

    var medicationName by remember { mutableStateOf("") }

    var dosage by remember { mutableStateOf("") }

    var expandedFreq by remember { mutableStateOf(false) }
    var selectedFreq by remember { mutableStateOf("") }
    val optionsFreq = listOf("Once daily", "Twice daily", "Three times daily", "As needed")

    val time = remember { mutableStateOf("") }
    val timePickerDialog = timePickerFun(time)

    var expandedType by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("") }
    val optionsType = listOf("Tablet", "Capsule", "Liquid", "Injection", "Topical", "Other")

    var notes by remember { mutableStateOf("") }


    val patientId: String = medicationsViewModel.getCurrentPatientId(context)

    val uiState by medicationsViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            //reset all fields
            medicationName = ""
            dosage = ""
            selectedFreq = ""
            time.value = ""
            selectedType = ""
            notes = ""

            snackbarHostState.showSnackbar("Medication saved!")
            context.startActivity(Intent(context, HomeActivity::class.java))

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //screen title
        Text(
            text = "Medication Details",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            //medication name input field
            OutlinedTextField(
                value = medicationName,
                onValueChange = {
                    medicationName = it
                },
                label = { Text("Medication Name") },
                isError = uiState.medicationNameError,
                trailingIcon = {
                    if (uiState.medicationNameError) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color.Red
                        )
                    }

                }
            )

            if (uiState.medicationNameError) {
                Text(
                    text = "Please fill in this blank",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 5.dp, top = 4.dp),

                    )
            }

            // dosage input field
            OutlinedTextField(
                value = dosage,
                onValueChange = {
                    dosage = it
                },
                label = { Text("Dosage") },
                isError = uiState.dosageError,
                trailingIcon = {
                    if (uiState.dosageError) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color.Red
                        )
                    }
                }
            )

            if (uiState.dosageBlankError) {
                Text(
                    text = "Please fill in this blank",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 5.dp, top = 4.dp),
                )
            }

            if (!uiState.dosageBlankError && uiState.dosageError) {
                Text(
                    text = "Must be a number followed by a unit",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 5.dp, top = 4.dp),
                )
            }

            //frequency dropdown field
            ExposedDropdownMenuBox(
                expanded = expandedFreq,
                onExpandedChange = { expandedFreq = !expandedFreq }
            ) {
                OutlinedTextField(
                    value = selectedFreq,
                    onValueChange = {},
                    isError = uiState.freqError,
                    readOnly = true,
                    label = { Text("Frequency") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFreq)
                    },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )

                ExposedDropdownMenu(
                    expanded = expandedFreq,
                    onDismissRequest = { expandedFreq = false }
                ) {
                    optionsFreq.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedFreq = option
                                expandedFreq = false
                            }
                        )
                    }
                }
            }

            if (uiState.freqError) {
                Text(
                    text = "Please fill in this blank",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 5.dp, top = 4.dp),

                    )
            }

            //time input field
            OutlinedTextField(
                value = time.value,
                onValueChange = {},
                label = { Text("Time") },
                isError = uiState.timeError,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick time")
                    }
                }
            )



            if (uiState.timeError) {
                Text(
                    text = "Please fill in this blank",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 5.dp, top = 4.dp),

                    )
            }

            //type dropdown field
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    isError = uiState.typeError,
                    label = { Text("Medication Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                    },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )

                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    optionsType.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedType = option
                                expandedType = false
                            }
                        )
                    }
                }
            }

            if (uiState.typeError) {
                Text(
                    text = "Please fill in this blank",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 5.dp, top = 4.dp),

                    )
            }

            //notes input field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") }
            )

            Spacer(Modifier.height(16.dp))
        }

        Row {
            // Reset all fields
            Button(
                onClick = {

                    medicationName = ""
                    dosage = ""
                    selectedFreq = ""
                    time.value = ""
                    selectedType = ""
                    notes = ""

                },
                modifier = Modifier.size(height = 40.dp, width = 100.dp)

            ) {
                Text("Clear")
            }

            Spacer(Modifier.width(10.dp))

            // save button
            Button(
                onClick = {
                    medicationsViewModel.validateAndSaveMedication(
                        patientId,
                        medicationName,
                        dosage,
                        selectedFreq,
                        time.value,
                        selectedType,
                        notes
                    )



                },
                modifier = Modifier.size(height = 40.dp, width = 100.dp)
            ) {
                Text("Save")
            }
        }


    }
}





