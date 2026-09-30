package com.wan.s34476474.medtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTipUiState
import com.wan.s34476474.medtrack.data.MedCoachTips.MedCoachTipViewModel
import com.wan.s34476474.medtrack.data.drugs.MedCoachDrugViewModel
import com.wan.s34476474.medtrack.data.medications.MedicationsViewModel
import com.wan.s34476474.medtrack.data.patients.PatientsViewModel
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedCoachActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val patientViewModel: PatientsViewModel = ViewModelProvider(
            this,
            PatientsViewModel.PatientsViewModelFactory(application)
        )[PatientsViewModel::class.java]

        val medicationViewModel: MedicationsViewModel = ViewModelProvider(
            this,
            MedicationsViewModel.MedicationsViewModelFactory(application)
        )[MedicationsViewModel::class.java]

        val medCoachTipViewModel: MedCoachTipViewModel = ViewModelProvider(
            this,
            MedCoachTipViewModel.MedCoachTipViewModelFactory(application)
        )[MedCoachTipViewModel::class.java]

        val medCoachDrugViewModel: MedCoachDrugViewModel = ViewModelProvider(
            this,
            MedCoachDrugViewModel.MedCoachDrugViewModelFactory(application)
        )[MedCoachDrugViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            MedtrackTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),



                    bottomBar = {
                        BottomBar()
                    }

                ) { innerPadding ->

                    MedCoachScreen(
                        innerPadding = innerPadding,
                        medicationsViewModel = medicationViewModel,
                        medCoachTipViewModel = medCoachTipViewModel,
                        medCoachDrugViewModel = medCoachDrugViewModel
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedCoachScreen(
    innerPadding: PaddingValues,
    medicationsViewModel: MedicationsViewModel,
    medCoachTipViewModel: MedCoachTipViewModel,
    medCoachDrugViewModel: MedCoachDrugViewModel
) {

    var medicationName by remember { mutableStateOf("") }
    var expandedItem by remember { mutableStateOf(false) }
    var showTipDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val patientId = medicationsViewModel.getCurrentPatientId(context)

    // Patient medications
    val optionsDrugItem by medicationsViewModel
        .getAllMedicationById(patientId)
        .collectAsState(initial = emptyList())

    // Drug information state
    val uiState by medCoachDrugViewModel.uiState.collectAsState()

    // GenAI tips state
    val tipState by medCoachTipViewModel.uiState.collectAsState()

    // Tip history
    val tips by medCoachTipViewModel
        .getTipsById(patientId)
        .collectAsState(initial = emptyList())

    val meds = optionsDrugItem.joinToString(", ") {
        "${it?.name} ${it?.dosage} (${it?.frequency})"
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Med Coach",
                fontSize = 30.sp,
                fontWeight = Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Learn more about your medications and get personalised tips.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }


        // =====================================================
        // DRUG INFORMATION
        // =====================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Section title
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Drug Information",
                        fontSize = 21.sp,
                        fontWeight = Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Check the purpose, warnings and dosage of a medication.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                // Medication selector
                if (optionsDrugItem.isEmpty()) {

                    OutlinedTextField(
                        value = medicationName,
                        onValueChange = {
                            medicationName = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Medication Name")
                        },
                        singleLine = true
                    )

                } else {

                    ExposedDropdownMenuBox(
                        expanded = expandedItem,
                        onExpandedChange = {
                            expandedItem = !expandedItem
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        OutlinedTextField(
                            value = medicationName,
                            onValueChange = {
                                medicationName = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    MenuAnchorType.PrimaryNotEditable,
                                    true
                                ),
                            isError = uiState.medicationNameError,
                            label = {
                                Text("Medication")
                            },
                            placeholder = {
                                Text("Select a medication")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandedItem
                                )
                            },
                            singleLine = true
                        )

                        ExposedDropdownMenu(
                            expanded = expandedItem,
                            onDismissRequest = {
                                expandedItem = false
                            }
                        ) {

                            optionsDrugItem.forEach { option ->

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option?.name ?: "Unknown medication"
                                        )
                                    },
                                    onClick = {
                                        medicationName =
                                            option?.name ?: ""

                                        expandedItem = false
                                    }
                                )
                            }
                        }
                    }
                }


                // Get information button
                Button(
                    onClick = {
                        medCoachDrugViewModel.searchDrug(
                            medicationName
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = medicationName.isNotBlank()
                ) {
                    Text(
                        text = "Get Information",
                        fontSize = 16.sp
                    )
                }


                // Loading
                if (uiState.isLoading) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }


                // Error
                if (uiState.error.isNotBlank()) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.errorContainer
                        )
                    ) {

                        Text(
                            text = uiState.error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }


                // Drug information
                if (
                    uiState.purpose.isNotBlank() ||
                    uiState.warnings.isNotBlank() ||
                    uiState.dosage.isNotBlank()
                ) {

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Information",
                        fontSize = 18.sp,
                        fontWeight = Bold
                    )


                    // Purpose
                    if (uiState.purpose.isNotBlank()) {

                        InformationCard(
                            title = "Purpose",
                            content = uiState.purpose
                        )
                    }


                    // Warnings
                    if (uiState.warnings.isNotBlank()) {

                        InformationCard(
                            title = "Warnings",
                            content = uiState.warnings
                        )
                    }


                    // Dosage
                    if (uiState.dosage.isNotBlank()) {

                        InformationCard(
                            title = "Dosage",
                            content = uiState.dosage
                        )
                    }
                }
            }
        }


        // =====================================================
        // GENAI TIPS
        // =====================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Section title
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "GenAI Tips",
                        fontSize = 21.sp,
                        fontWeight = Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Generate helpful medication tips based on your current medications.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                // Generate button
                Button(
                    onClick = {
                        medCoachTipViewModel.sendPrompt(
                            patientId,
                            meds
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = optionsDrugItem.isNotEmpty()
                ) {

                    Text(
                        text = "Generate Tips",
                        fontSize = 16.sp
                    )
                }


                // Generated tip
                Text(
                    text = "Latest Tip",
                    fontSize = 18.sp,
                    fontWeight = Bold
                )


                when (val state = tipState) {

                    is MedCoachTipUiState.Loading -> {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            CircularProgressIndicator()
                        }
                    }


                    is MedCoachTipUiState.Success -> {

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {

                            Text(
                                text = state.outputText,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }


                    is MedCoachTipUiState.Error -> {

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.errorContainer
                            )
                        ) {

                            Text(
                                text = state.errorMessage,
                                modifier = Modifier.padding(16.dp),
                                color =
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }


                    else -> {

                        Text(
                            text = "No tip generated yet.",
                            fontSize = 14.sp,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }


                // Show history
                OutlinedButton(
                    onClick = {
                        showTipDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text("View Tip History")
                }
            }
        }


        // =====================================================
        // BOTTOM SPACING
        // =====================================================

        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }


    // =========================================================
    // TIP HISTORY DIALOG
    // =========================================================

    if (showTipDialog) {

        AlertDialog(
            onDismissRequest = {
                showTipDialog = false
            },

            title = {
                Text(
                    text = "Tip History",
                    fontWeight = Bold
                )
            },

            text = {

                if (tips.isEmpty()) {

                    Text(
                        text = "No tips generated yet."
                    )

                } else {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .verticalScroll(
                                rememberScrollState()
                            )
                    ) {

                        tips.reversed().forEach { tip ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {

                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(6.dp)
                                ) {

                                    Text(
                                        text = tip.message,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )

                                    Text(
                                        text = SimpleDateFormat(
                                            "dd/MM/yyyy HH:mm",
                                            Locale.getDefault()
                                        ).format(
                                            Date(tip.timestamp)
                                        ),
                                        style =
                                            MaterialTheme.typography.bodySmall,
                                        color =
                                            MaterialTheme.colorScheme
                                                .onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },

            confirmButton = {},

            dismissButton = {

                OutlinedButton(
                    onClick = {
                        showTipDialog = false
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }
}


// =============================================================
// REUSABLE INFORMATION CARD
// =============================================================

@Composable
fun InformationCard(
    title: String,
    content: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = Bold
            )

            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

