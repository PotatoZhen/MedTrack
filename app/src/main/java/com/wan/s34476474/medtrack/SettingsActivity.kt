package com.wan.s34476474.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.room.util.TableInfo
import com.wan.s34476474.medtrack.data.medications.MedicationsViewModel
import com.wan.s34476474.medtrack.data.patients.PatientsViewModel
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val patientViewModel: PatientsViewModel = ViewModelProvider(
            this, PatientsViewModel.PatientsViewModelFactory(application)
        )[PatientsViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MedtrackTheme {
                Scaffold(
                    bottomBar = { BottomBar() },
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingsScreen(
                        modifier = Modifier.padding(innerPadding),
                        patientsViewModel = patientViewModel,
                    )
                }
            }
        }
    }
}


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    patientsViewModel: PatientsViewModel,
) {
    val context = LocalContext.current
    val patientId: String = patientsViewModel.getCurrentPatientId(context)

    val patientName by patientsViewModel
        .getPatientName(patientId)
        .collectAsState(initial = "")

    val patientPhoneNumber by patientsViewModel
        .getPatientPhoneNumber(patientId)
        .collectAsState(initial = "")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // -------------------------
        // Header
        // -------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Settings",
                fontSize = 30.sp,
                fontWeight = Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Manage your account and preferences",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }


        // -------------------------
        // Patient Profile Card
        // -------------------------
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Patient Profile",
                    fontSize = 20.sp,
                    fontWeight = Bold
                )

                // Name
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Name",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = patientName ?: "Not available",
                        fontSize = 17.sp,
                        fontWeight = Bold
                    )
                }

                // Phone
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Phone Number",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = patientPhoneNumber ?: "Not available",
                        fontSize = 17.sp
                    )
                }

                // Patient ID
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Patient ID",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = patientId,
                        fontSize = 17.sp
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))


        // -------------------------
        // Account Section
        // -------------------------
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Account",
                fontSize = 20.sp,
                fontWeight = Bold
            )

            Text(
                text = "Manage your MedTrack account",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Logout
            Button(
                onClick = {
                    patientsViewModel.logout(context)

                    val intent = Intent(
                        context,
                        LoginActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Log Out",
                    fontSize = 16.sp
                )
            }


            // Clinician Login
            Button(
                onClick = {
                    val intent = Intent(
                        context,
                        ClinicianLoginActivity::class.java
                    )

                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Clinician Login",
                    fontSize = 16.sp
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))


        // -------------------------
        // App Information
        // -------------------------
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "MedTrack",
                    fontSize = 16.sp,
                    fontWeight = Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Your health, organised.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

