package com.wan.s34476474.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme
import androidx.lifecycle.ViewModelProvider
import com.wan.s34476474.medtrack.data.patients.PatientsViewModel
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val patientViewModel: PatientsViewModel = ViewModelProvider(
            this, PatientsViewModel.PatientsViewModelFactory(application)
        )[PatientsViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MedtrackTheme {
                Surface {
                    LoginScreenLayout(patientsViewModel = patientViewModel)
                }
            }
        }
    }
}



@Composable
fun LoginScreenLayout(patientsViewModel: PatientsViewModel) {

    var patientID by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val state by patientsViewModel.loginState.collectAsState()

    LaunchedEffect(state) {

        if (state.success) {

            val sharedPref = context.getSharedPreferences(
                "logged_in_patient_id",
                Context.MODE_PRIVATE
            ).edit()

            sharedPref.putString("patient_id", patientID).apply()

            Toast.makeText(
                context,
                "Login Successful",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }

        state.error?.let {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // MedTrack Logo
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.medtrack),
                contentDescription = "MedTrack Logo",
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Welcome Back",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Log in to your MedTrack account",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Patient ID
            OutlinedTextField(
                value = patientID,
                onValueChange = {
                    patientID = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Patient ID")
                },
                placeholder = {
                    Text("Enter your Patient ID")
                },
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Password")
                },
                placeholder = {
                    Text("Enter your password")
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Error message
            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Login button
            Button(
                onClick = {
                    patientsViewModel.login(patientID, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                enabled = patientID.isNotBlank() && password.isNotBlank()
            ) {
                Text(
                    text = "Login",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Account activation
            Text(
                text = "First-time login? Activate your account.",
                color = Color(0xFF1976D2),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                modifier = Modifier.clickable {
                    context.startActivity(
                        Intent(
                            context,
                            ClaimAccountActivity::class.java
                        )
                    )
                }
            )
        }

        // Student ID
        Text(
            text = "Ling Wan Zhen • 34476474",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp),
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}







