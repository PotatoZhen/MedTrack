package com.wan.s34476474.medtrack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.wan.s34476474.medtrack.data.patients.PatientsViewModel
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme
import kotlinx.coroutines.launch

class ClaimAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val patientViewModel: PatientsViewModel = ViewModelProvider(
            this, PatientsViewModel.PatientsViewModelFactory(application)
        )[PatientsViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MedtrackTheme {
                Surface() {
                    SetPasswordScreen(patientsViewModel = patientViewModel)
                }
            }
        }
    }
}

@Composable
fun SetPasswordScreen(patientsViewModel: PatientsViewModel) {
    var patientID by remember { mutableStateOf("") }

    var phoneNumber by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    val state by patientsViewModel.claimState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state) {

        if (state.success) {
            Toast.makeText(context, "Account claimed successfully", Toast.LENGTH_SHORT).show()

            context.startActivity(
                Intent(context, LoginActivity::class.java)
            )
        }

        state.generalError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Activation",
            fontWeight = FontWeight.Bold,
            fontSize = 35.sp,
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = patientID,
            onValueChange = {
                patientID = it
            },
            label = { Text("Enter Patient ID") },
            singleLine = true
        )



        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
            },
            label = { Text("Enter Phone Number") },
            singleLine = true
        )



        Spacer(Modifier.height(20.dp))

        //password input field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Enter Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )



        Spacer(Modifier.height(20.dp))

        state.generalError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(onClick = {

            patientsViewModel.claimAccount(patientID, phoneNumber, password)

        }) {
            Text("Claim")
        }

    }

}


