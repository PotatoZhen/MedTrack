package com.wan.s34476474.medtrack

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.wan.s34476474.medtrack.data.medications.Medication
import com.wan.s34476474.medtrack.data.patients.PatientsViewModel
import com.wan.s34476474.medtrack.data.symptoms.Symptom
import com.wan.s34476474.medtrack.data.symptoms.SymptomsViewModel
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme
import com.wan.s34476474.medtrack.utils.dateTimePickerFun
import com.wan.s34476474.medtrack.utils.getSeverityColor
import com.wan.s34476474.medtrack.utils.getSeverityLabel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.sequences.forEach

class SymptomActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val symptomsViewModel: SymptomsViewModel = ViewModelProvider(
            this, SymptomsViewModel.SymptomsViewModelFactory(application)
        )[SymptomsViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MedtrackTheme {
                Scaffold(
                    bottomBar = { BottomBar() },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    SymptomScreenLayout(
                        innerPadding, snackbarHostState = snackbarHostState,
                        symptomsViewModel = symptomsViewModel
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomScreenLayout(
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    symptomsViewModel: SymptomsViewModel
) {
    val context = LocalContext.current

    // -------------------------
    // Form state
    // -------------------------

    var expandedCategory by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf("")
    }

    val optionsCategory = listOf(
        "Pain",
        "Nausea",
        "Dizziness",
        "Fatigue",
        "Headache",
        "Skin Reaction",
        "Other"
    )

    var severityRating by remember {
        mutableStateOf(1f)
    }

    var severityRatingError by remember {
        mutableStateOf(false)
    }

    val severity = severityRating.roundToInt()

    val severityColor = getSeverityColor(severity)

    var notes by remember {
        mutableStateOf("")
    }

    val dateTime = remember {
        mutableStateOf("")
    }

    // -------------------------
    // Patient
    // -------------------------

    val patientId =
        symptomsViewModel.getCurrentPatientId(context)

    val symptoms by symptomsViewModel
        .getAllSymptomsById(patientId)
        .collectAsState(initial = emptyList())

    val uiState by symptomsViewModel
        .uiState
        .collectAsState()

    // -------------------------
    // Snackbar / reset
    // -------------------------

    LaunchedEffect(uiState) {

        if (uiState.success) {

            snackbarHostState.showSnackbar(
                "Symptom saved successfully!"
            )

            selectedCategory = ""
            notes = ""
            dateTime.value = ""
            severityRating = 1f
            severityRatingError = false
        }

        uiState.generalError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // -------------------------
    // Main screen
    // -------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // =====================================================
        // HEADER
        // =====================================================

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = "Symptoms",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Track how you're feeling and keep a record of your symptoms.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // =====================================================
        // RECORD SYMPTOM CARD
        // =====================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                // -------------------------
                // Card header
                // -------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Record a Symptom",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = "Add details about how you are feeling.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme
                                .onSurfaceVariant
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                // =================================================
                // CATEGORY
                // =================================================

                Text(
                    text = "Symptom Category",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = {
                        expandedCategory = !expandedCategory
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        isError = uiState.categoryError,
                        readOnly = true,
                        label = {
                            Text("Select a symptom")
                        },
                        placeholder = {
                            Text("Choose category")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded = expandedCategory
                                )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                MenuAnchorType.PrimaryNotEditable,
                                true
                            ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = {
                            expandedCategory = false
                        }
                    ) {

                        optionsCategory.forEach { option ->

                            DropdownMenuItem(
                                text = {
                                    Text(option)
                                },
                                onClick = {

                                    selectedCategory =
                                        option

                                    expandedCategory =
                                        false
                                }
                            )
                        }
                    }
                }

                if (uiState.categoryError) {

                    Text(
                        text = "* Required field",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(
                            top = 4.dp
                        )
                    )
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // =================================================
                // NOTES
                // =================================================

                Text(
                    text = "Notes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = {
                        notes = it
                    },
                    label = {
                        Text("Additional notes")
                    },
                    placeholder = {
                        Text(
                            "Describe what you are experiencing..."
                        )
                    },
                    isError = uiState.notesError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.End
                ) {

                    Text(
                        text = "${notes.length} / 200",
                        fontSize = 12.sp,
                        color =
                            if (notes.length > 200)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                    )
                }

                if (uiState.notesError) {

                    Text(
                        text =
                            "* Maximum 200 characters allowed",
                        color =
                            MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                // =================================================
                // DATE & TIME
                // =================================================

                Text(
                    text = "Date & Time",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                OutlinedTextField(
                    value = dateTime.value,
                    onValueChange = {},
                    isError = uiState.dateTimeError,
                    label = {
                        Text("When did this occur?")
                    },
                    placeholder = {
                        Text("Select date and time")
                    },
                    readOnly = true,
                    trailingIcon = {

                        IconButton(
                            onClick = {
                                dateTimePickerFun(
                                    dateTime,
                                    context
                                )
                            }
                        ) {

                            Icon(
                                Icons.Default.DateRange,
                                contentDescription =
                                    "Pick date and time"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                if (uiState.dateTimeError) {

                    Text(
                        text =
                            "* Date and time are required",
                        color =
                            MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(
                            top = 4.dp
                        )
                    )
                }


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                // =================================================
                // SEVERITY
                // =================================================

                Text(
                    text = "Severity",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "1",
                        fontSize = 12.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )

                    Slider(
                        value = severityRating,
                        onValueChange = {
                            severityRating = it
                            severityRatingError = false
                        },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = severityColor,
                            activeTrackColor = severityColor,
                            inactiveTrackColor =
                                severityColor.copy(
                                    alpha = 0.25f
                                )
                        )
                    )

                    Text(
                        text = "10",
                        fontSize = 12.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                // Severity badge

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            severityColor.copy(
                                alpha = 0.12f
                            )
                    )
                ) {

                    Text(
                        text =
                            "$severity / 10  •  ${
                                getSeverityLabel(
                                    severity
                                )
                            }",
                        color = severityColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                    )
                }

                if (severityRatingError) {

                    Text(
                        text =
                            "Please select a severity level",
                        color =
                            MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }


                Spacer(
                    modifier = Modifier.height(22.dp)
                )


                // =================================================
                // SAVE BUTTON
                // =================================================

                Button(
                    onClick = {

                        symptomsViewModel
                            .validateAndSaveSymptom(
                                patientId = patientId,
                                category = selectedCategory,
                                dateTime = dateTime.value,
                                notes = notes,
                                severity = severity
                            )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        text = "Save Symptom",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // =====================================================
        // HISTORY HEADER
        // =====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Symptom History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        "${symptoms.size} record" +
                                if (symptoms.size != 1)
                                    "s"
                                else
                                    "",
                    fontSize = 13.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // =====================================================
        // HISTORY CARD
        // =====================================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            )
        ) {

            if (symptoms.isEmpty()) {

                // -------------------------
                // Empty state
                // -------------------------

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(35.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "♡",
                        fontSize = 40.sp,
                        color =
                            MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "No symptoms logged",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Your symptom history will appear here.",
                        fontSize = 13.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

            } else {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {

                    symptoms.forEach { symptom ->

                        val symptomSeverity =
                            symptom?.severity
                                ?.toIntOrNull()
                                ?: 0

                        val symptomColor =
                            getSeverityColor(
                                symptomSeverity
                            )

                        // -------------------------
                        // Individual history item
                        // -------------------------

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 6.dp
                                ),
                            shape =
                                RoundedCornerShape(16.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .surfaceVariant
                                )
                        ) {

                            Column(
                                modifier =
                                    Modifier.padding(14.dp)
                            ) {

                                // Top row

                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth(),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Column(
                                        modifier =
                                            Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text =
                                                symptom?.category
                                                    ?: "Unknown",
                                            fontSize = 17.sp,
                                            fontWeight =
                                                FontWeight.Bold
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.height(3.dp)
                                        )

                                        Text(
                                            text =
                                                symptom?.dateTime
                                                    ?: "",
                                            fontSize = 12.sp,
                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurfaceVariant
                                        )
                                    }

                                    // Severity badge

                                    Card(
                                        shape =
                                            RoundedCornerShape(
                                                10.dp
                                            ),
                                        colors =
                                            CardDefaults
                                                .cardColors(
                                                    containerColor =
                                                        symptomColor
                                                            .copy(
                                                                alpha =
                                                                    0.12f
                                                            )
                                                )
                                    ) {

                                        Text(
                                            text =
                                                "$symptomSeverity/10",
                                            color =
                                                symptomColor,
                                            fontWeight =
                                                FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier =
                                                Modifier.padding(
                                                    horizontal = 9.dp,
                                                    vertical = 6.dp
                                                )
                                        )
                                    }
                                }

                                // Notes

                                if (!symptom?.notes
                                        .isNullOrBlank()
                                ) {

                                    Spacer(
                                        modifier =
                                            Modifier.height(10.dp)
                                    )

                                    Text(
                                        text =
                                            symptom?.notes
                                                ?: "",
                                        fontSize = 13.sp,
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .onSurfaceVariant
                                    )
                                }

                                Spacer(
                                    modifier =
                                        Modifier.height(6.dp)
                                )

                                Text(
                                    text =
                                        getSeverityLabel(
                                            symptomSeverity
                                        ),
                                    fontSize = 12.sp,
                                    fontWeight =
                                        FontWeight.Medium,
                                    color =
                                        symptomColor
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

