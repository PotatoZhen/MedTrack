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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme

class ClinicianLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedtrackTheme {
                Surface() {
                    ClinicianLoginScreen()
                }
            }
        }
    }
}

@Composable
fun ClinicianLoginScreen() {

    var accessKey by remember { mutableStateOf("") }
    var errorMessage by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Clinician Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = accessKey ,
            onValueChange = {accessKey = it},
            label = {Text("Access Key")},
            isError = errorMessage.isNotBlank()
        )

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                if (accessKey == "dollar-entry-apples") {
                    context.startActivity(Intent(context, ClinicianDashboardActivity::class.java))
                } else {
                    errorMessage = "Invalid access key"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Login")
        }
    }




}
