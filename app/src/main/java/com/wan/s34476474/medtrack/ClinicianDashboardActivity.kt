package com.wan.s34476474.medtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.wan.s34476474.medtrack.data.clinicianDashboard.ClinicianUiState
import com.wan.s34476474.medtrack.data.clinicianDashboard.ClinicianViewModel
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme

class ClinicianDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val clinicianViewModel: ClinicianViewModel = ViewModelProvider(
            this, ClinicianViewModel.ClinicianViewModelFactory(application)
        )[ClinicianViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MedtrackTheme {
                Scaffold(
                ) { innerPadding ->
                    ClinicianDashboardScreen(
                        innerPadding,
                        clinicianViewModel = clinicianViewModel)

                }
            }
        }
    }
}

@Composable
fun ClinicianDashboardScreen(
    innerPadding: PaddingValues,
    clinicianViewModel: ClinicianViewModel
) {



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Text(
            text = "Clinician Dashboard",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = Bold,
            fontSize = 30.sp
        )

        Spacer(Modifier.height(20.dp))

        AggregateStatsCard(clinicianViewModel)

        GenAIInsightsCard(clinicianViewModel)

    }
}

@Composable
fun AggregateStatsCard(
     clinicianViewModel: ClinicianViewModel
    ) {


    val stats by clinicianViewModel.stats.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {


        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Aggregate Statistics",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center)

            Spacer(Modifier.height(10.dp))

            Text("Total Patients: ${stats.totalPatients}")
            Text("Average Meds per patient: ${ String.format("%.2f", stats.avgMedicationsPerPatient)}")
            Text("Most Symptom: ${stats.mostCommonSymptom}")
            Text("Average Symptom Severity: ${stats.avgSymptomSeverity}")
        }
    }

    Spacer(Modifier.height(30.dp))

}

@Composable
fun GenAIInsightsCard(clinicianViewModel: ClinicianViewModel) {


    val state by clinicianViewModel.uiState.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "GenAI Insights",
                fontWeight = Bold,
                fontSize = 20.sp
            )

            Button(onClick = {

                clinicianViewModel.generateInsights()

            }) {
                Text("Find Patterns")
            }

            when (state) {

                is ClinicianUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is ClinicianUiState.Success -> {
                    val list = (state as ClinicianUiState.Success).insights
                    list.forEach {
                        Text("• $it\n")
                    }
                }

                is ClinicianUiState.Error -> {
                    Text(
                        text = (state as ClinicianUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    Text("No insights yet")
                }
            }
        }

        }

}

