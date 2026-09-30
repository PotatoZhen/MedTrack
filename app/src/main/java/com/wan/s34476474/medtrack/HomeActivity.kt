package com.wan.s34476474.medtrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.ViewModelProvider
import com.wan.s34476474.medtrack.data.drugsInteraction.InteractionUiState
import com.wan.s34476474.medtrack.data.drugsInteraction.InteractionViewModel
import com.wan.s34476474.medtrack.data.medications.MedicationsViewModel
import com.wan.s34476474.medtrack.data.patients.PatientsViewModel
import com.wan.s34476474.medtrack.data.takenstatus.TakenStatusViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val patientViewModel: PatientsViewModel = ViewModelProvider(
            this, PatientsViewModel.PatientsViewModelFactory(application)
        )[PatientsViewModel::class.java]

        val medicationViewModel: MedicationsViewModel = ViewModelProvider(
            this, MedicationsViewModel.MedicationsViewModelFactory(application)
        )[MedicationsViewModel::class.java]

        val takenStatusViewModel: TakenStatusViewModel = ViewModelProvider(
            this, TakenStatusViewModel.TakenStatusViewModelFactory(application)
        )[TakenStatusViewModel::class.java]

        val interactionViewModel: InteractionViewModel = ViewModelProvider(
            this,
            InteractionViewModel.InteractionViewModelFactory(application)
        )[InteractionViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MedtrackTheme {
                Scaffold(
                    topBar = { TopBar(patientsViewModel = patientViewModel) },
                    bottomBar = { BottomBar() }
                ) { innerPadding ->
                    HomeScreenLayout(
                        modifier = Modifier.padding(innerPadding),
                        patientsViewModel = patientViewModel,
                        medicationsViewModel = medicationViewModel,
                        takenStatusViewModel = takenStatusViewModel,
                        interactionViewModel = interactionViewModel
                    )
                }
            }
        }


    }

}

/*
Bottom navigation bar
 */
@Composable
fun BottomBar() {
    val context = LocalContext.current
    BottomAppBar(
        modifier = Modifier.height(60.dp),
        content = {
            IconButton(onClick = {
                context.startActivity(Intent(context, HomeActivity::class.java))

            }) {
                Icon(Icons.Filled.Home, contentDescription = "Go Home")
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,

            ) {
                IconButton(onClick = {
                    context.startActivity(Intent(context, SymptomActivity::class.java))

                }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Symptoms")

                }


            }

            IconButton(onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))

            }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }

            IconButton(onClick = {
                context.startActivity(Intent(context, MedCoachActivity::class.java))

            }) {
                Icon(Icons.Filled.Person, contentDescription = "Med Coach")
            }
        }
    )

}

/*
Top bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    patientsViewModel: PatientsViewModel
) {
    val context = LocalContext.current

    CenterAlignedTopAppBar(
        modifier = Modifier.height(100.dp),
        title = {
            Text(
                "Home",
                fontSize = 30.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.LightGray,
            titleContentColor = Color.Black
        ),
        actions = {
            Button(
                onClick = {
                    patientsViewModel.logout(context)
                    val intent = Intent(context, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                modifier = Modifier.size(width = 100.dp, height = 40.dp)
            ) {
                Text("Log Out")
            }
        }


    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenLayout(
    modifier: Modifier = Modifier,
    patientsViewModel: PatientsViewModel,
    medicationsViewModel: MedicationsViewModel,
    takenStatusViewModel: TakenStatusViewModel,
    interactionViewModel: InteractionViewModel
) {

    val context = LocalContext.current

    // -------------------------
    // Patient information
    // -------------------------

    val format = SimpleDateFormat(
        "EEEE, d MMMM yyyy",
        Locale.getDefault()
    )

    val date = format.format(Date())

    val patientId =
        patientsViewModel.getCurrentPatientId(context)

    val patientName by patientsViewModel
        .getPatientName(patientId)
        .collectAsState(initial = "")

    // -------------------------
    // Medication information
    // -------------------------

    val medications by medicationsViewModel
        .getAllMedicationById(patientId)
        .collectAsState(initial = emptyList())

    val today = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    ).format(Date())

    val takenStatuses by takenStatusViewModel
        .getToday(patientId, today)
        .collectAsState(initial = emptyList())

    // -------------------------
    // Drug interaction state
    // -------------------------

    var medicationName1 by remember {
        mutableStateOf("")
    }

    var medicationName2 by remember {
        mutableStateOf("")
    }

    var expandedItem1 by remember {
        mutableStateOf(false)
    }

    var expandedItem2 by remember {
        mutableStateOf(false)
    }

    val interactionState by interactionViewModel
        .uiState
        .collectAsState()

    // -------------------------
    // Main screen
    // -------------------------

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // =========================
        // GREETING
        // =========================

        Text(
            text = "Hello 👋",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = patientName?.ifBlank {
                "Welcome back"
            } ?: "Welcome back",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = date,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))


        // =========================
        // MEDICATION CARD
        // =========================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                // Header

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Today's Medications",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = "${medications.size} medication" +
                                    if (medications.size != 1)
                                        "s"
                                    else
                                        "",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme
                                .onSurfaceVariant
                        )
                    }

                    // Add medication button

                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context,
                                    MedicationActivity::class.java
                                )
                            )
                        },
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {

                        Text(
                            text = "+",
                            fontSize = 25.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // =========================
                // MEDICATION LIST
                // =========================

                if (medications.isEmpty()) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 30.dp
                            ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "💊",
                            fontSize = 40.sp
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Text(
                            text = "No medications scheduled",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "Your medication schedule is empty.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme
                                .onSurfaceVariant
                        )
                    }

                } else {

                    val takenMap =
                        takenStatusViewModel
                            .getTakenMap(takenStatuses)

                    medications.forEach { med ->

                        val isTaken =
                            takenMap[med?.id] == true

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 5.dp
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    if (isTaken)
                                        MaterialTheme
                                            .colorScheme
                                            .surfaceVariant
                                    else
                                        MaterialTheme
                                            .colorScheme
                                            .primaryContainer
                            ),
                            elevation =
                                CardDefaults.cardElevation(
                                    defaultElevation = 1.dp
                                )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Checkbox(
                                    checked = isTaken,
                                    onCheckedChange = {

                                        med?.let {

                                            takenStatusViewModel
                                                .toggleTaken(
                                                    medicationId =
                                                        it.id,
                                                    patientId =
                                                        patientId,
                                                    date = today,
                                                    currentValue =
                                                        isTaken
                                                )
                                        }
                                    }
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(4.dp)
                                )

                                Column(
                                    modifier =
                                        Modifier.weight(1f)
                                ) {

                                    Text(
                                        text =
                                            med?.name
                                                ?: "Unknown medication",
                                        fontSize = 17.sp,
                                        fontWeight =
                                            FontWeight.SemiBold,
                                        textDecoration =
                                            if (isTaken)
                                                TextDecoration
                                                    .LineThrough
                                            else
                                                TextDecoration.None
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(3.dp)
                                    )

                                    Text(
                                        text =
                                            "${med?.dosage} • " +
                                                    "${med?.frequency}",
                                        fontSize = 13.sp,
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .onSurfaceVariant,
                                        textDecoration =
                                            if (isTaken)
                                                TextDecoration
                                                    .LineThrough
                                            else
                                                TextDecoration.None
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(2.dp)
                                    )

                                    Text(
                                        text =
                                            "🕐 ${med?.time}",
                                        fontSize = 13.sp,
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .onSurfaceVariant
                                    )
                                }

                                if (isTaken) {

                                    Text(
                                        text = "Taken",
                                        fontSize = 12.sp,
                                        fontWeight =
                                            FontWeight.Bold,
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // =========================
        // DRUG INTERACTION CARD
        // =========================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                // Header

                Text(
                    text = "⚕",
                    fontSize = 32.sp,
                    color =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Drug Interaction",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Check whether two medications may interact.",
                    fontSize = 13.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                // =========================
                // NOT ENOUGH MEDICATIONS
                // =========================

                if (medications.size < 2) {

                    Text(
                        text =
                            "Add at least two medications " +
                                    "to check for interactions.",
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 25.dp
                        ),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )

                } else {

                    // =========================
                    // MEDICATION 1
                    // =========================

                    ExposedDropdownMenuBox(
                        expanded = expandedItem1,
                        onExpandedChange = {
                            expandedItem1 = !expandedItem1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        OutlinedTextField(
                            value = medicationName1,
                            onValueChange = {
                                medicationName1 = it
                            },
                            label = {
                                Text("First medication")
                            },
                            placeholder = {
                                Text("Select medication")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults
                                    .TrailingIcon(
                                        expanded =
                                            expandedItem1
                                    )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    MenuAnchorType
                                        .PrimaryNotEditable,
                                    true
                                ),
                            singleLine = true,
                            shape =
                                RoundedCornerShape(14.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedItem1,
                            onDismissRequest = {
                                expandedItem1 = false
                            }
                        ) {

                            medications.forEach { option ->

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option?.name
                                                ?: "Unknown"
                                        )
                                    },
                                    onClick = {

                                        medicationName1 =
                                            option?.name
                                                ?: ""

                                        expandedItem1 = false
                                    }
                                )
                            }
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )


                    // =========================
                    // MEDICATION 2
                    // =========================

                    ExposedDropdownMenuBox(
                        expanded = expandedItem2,
                        onExpandedChange = {
                            expandedItem2 = !expandedItem2
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        OutlinedTextField(
                            value = medicationName2,
                            onValueChange = {
                                medicationName2 = it
                            },
                            label = {
                                Text("Second medication")
                            },
                            placeholder = {
                                Text("Select medication")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults
                                    .TrailingIcon(
                                        expanded =
                                            expandedItem2
                                    )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    MenuAnchorType
                                        .PrimaryNotEditable,
                                    true
                                ),
                            singleLine = true,
                            shape =
                                RoundedCornerShape(14.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expandedItem2,
                            onDismissRequest = {
                                expandedItem2 = false
                            }
                        ) {

                            medications.forEach { option ->

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            option?.name
                                                ?: "Unknown"
                                        )
                                    },
                                    onClick = {

                                        medicationName2 =
                                            option?.name
                                                ?: ""

                                        expandedItem2 = false
                                    }
                                )
                            }
                        }
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================
                    // CHECK BUTTON
                    // =========================

                    Button(
                        onClick = {

                            interactionViewModel
                                .sendPrompt(
                                    medicationName1,
                                    medicationName2
                                )
                        },
                        enabled =
                            medicationName1.isNotBlank() &&
                                    medicationName2.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Text(
                            text = "Check Interaction",
                            fontSize = 16.sp,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }


                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // =========================
                    // RESULT
                    // =========================

                    when (val state = interactionState) {

                        is InteractionUiState.Loading -> {

                            CircularProgressIndicator(
                                modifier = Modifier.padding(
                                    20.dp
                                )
                            )
                        }

                        is InteractionUiState.Success -> {

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape =
                                    RoundedCornerShape(14.dp),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            MaterialTheme
                                                .colorScheme
                                                .secondaryContainer
                                    )
                            ) {

                                Text(
                                    text = state.outputText,
                                    modifier = Modifier.padding(
                                        14.dp
                                    ),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        is InteractionUiState.Error -> {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape =
                                    RoundedCornerShape(14.dp),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            MaterialTheme
                                                .colorScheme
                                                .errorContainer
                                    )
                            ) {

                                Text(
                                    text =
                                        state.errorMessage,
                                    modifier = Modifier.padding(
                                        14.dp
                                    ),
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onErrorContainer,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        else -> {}
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}






