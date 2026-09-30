package com.wan.s34476474.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wan.s34476474.medtrack.data.patients.Patient
import com.wan.s34476474.medtrack.data.patients.PatientsViewModel
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID
import kotlin.sequences.forEach

class SignUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val patientViewModel: PatientsViewModel = ViewModelProvider(
            this, PatientsViewModel.PatientsViewModelFactory(application)
        )[PatientsViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MedtrackTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    SignUpScreenLayout(
                        innerPadding, snackbarHostState = snackbarHostState,
                        patientsViewModel = patientViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SignUpScreenLayout(
    innerPadding: PaddingValues, snackbarHostState: SnackbarHostState,
    patientsViewModel: PatientsViewModel
) {
    val context = LocalContext.current

    //input fields
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    val state by patientsViewModel.signUpState.collectAsState()

    LaunchedEffect(state) {
        if (state.success) {
            snackbarHostState.showSnackbar("Register Successfully")
            context.startActivity(Intent(context, LoginActivity::class.java))
        }

        state.generalError?.let {
            snackbarHostState.showSnackbar(it)
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //screen title
        Text(
            text = "Sign Up",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )

        //name input field
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            isError = state.nameError,
            label = { Text("Full Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        )

        if (state.nameError) {
            Text(
                text = "* Please fill in this blank",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)

            )
        }

        //phone number input fields
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it

            },
            isError = state.phoneEmptyError || state.phoneFormatError,
            label = { Text("Phone Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)

        )

        if (state.phoneEmptyError) {
            Text(
                text = "* Please fill in this blank",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)

            )
        }

        if (state.phoneFormatError) {
            Text(
                text = "* Must be 10 digits (start 04)",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
        }


        // password input field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            isError = state.passwordEmptyError ||
                    state.passwordLengthError ||
                    state.passwordLetterError ||
                    state.passwordDigitError,
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)

        )

        if (state.passwordEmptyError) {
            Text(
                text = "* Please fill in this blank",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)

            )
        }

        if (state.passwordLengthError) {
            Text(
                "* At least 8 characters",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
        }

        if (state.passwordLetterError) {
            Text(
                "* Must contain a letter",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
        }

        if (state.passwordDigitError) {
            Text(
                "* Must contain a number",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
        }

        // confirm password input field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
            },
            isError = state.confirmPasswordError || state.confirmPasswordEmptyError,
            label = { Text("Confirm Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        )

        if (state.confirmPasswordEmptyError) {
            Text(
                text = "* Please fill in this blank",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)

            )
        }

        if (state.confirmPasswordError) {
            Text(
                text = "* Please confirm your password",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)

            )
        }


        Button(
            onClick = {

                patientsViewModel.validateAndRegister(name, phoneNumber, password, confirmPassword)
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Sign Up")
        }

    }

}


