package com.wan.s34476474.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wan.s34476474.medtrack.ui.theme.MedtrackTheme
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.wan.s34476474.medtrack.data.patients.Patient
import com.wan.s34476474.medtrack.data.patients.PatientsViewModel
import com.wan.s34476474.medtrack.utils.DatabaseSeeder
import com.wan.s34476474.medtrack.utils.SeedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.sequences.forEach

class WelcomeActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val seedViewModel = ViewModelProvider(this)[SeedViewModel::class.java]
        seedViewModel.seedIfNeeded()

        enableEdgeToEdge()
        //Retrieve saved login state
        val prefs = getSharedPreferences("logged_in_patient_id", MODE_PRIVATE)
        val patientId = prefs.getString("patient_id", null)

        setContent {

            MedtrackTheme {
                Surface {
                    if (patientId != null) {
                        startActivity(Intent(this, HomeActivity::class.java))
                    } else {
                        WelcomeScreen()
                    }
                }
            }
        }
    }
}

/*
Welcome screen shown to users who are not logged in.
 */

@Composable
fun WelcomeScreen() {

    val context = LocalContext.current

    Box(
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

            // App Logo
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.medtrack),
                contentDescription = "MedTrack Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // App Name
            Text(
                text = "MedTrack",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your health, organised.",
                fontSize = 17.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Disclaimer Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF3F8FC),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Important Notice",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This app is for tracking purposes only and does not replace professional medical advice.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Monash Health Link
            Text(
                text = "Visit Monash Health",
                color = Color(0xFF1976D2),
                textDecoration = TextDecoration.Underline,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://monashhealth.org/".toUri()
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Login Button
            Button(
                onClick = {
                    context.startActivity(
                        Intent(context, LoginActivity::class.java)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Login",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sign Up Button
            Button(
                onClick = {
                    context.startActivity(
                        Intent(context, SignUpActivity::class.java)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
            ) {
                Text(
                    text = "Create an Account",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1976D2)
                )
            }
        }

        /*
        // Student ID
        Text(
            text = "Ling Wan Zhen • 34476474",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp),
            fontSize = 13.sp,
            color = Color.Gray
        )

         */
    }
}






