package com.wan.s34476474.medtrack.data.patients

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wan.s34476474.medtrack.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PatientsViewModel(application: Application) : ViewModel() {

    val repository = PatientsRepository(application)

    fun insertAll(patients: List<Patient>) {
        viewModelScope.launch {
            repository.insertAll(patients)
        }
    }



    // Holds login UI state
    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> =
        _loginState.asStateFlow()

    // Handles patient login validation and authentication flow
    fun login(patientID: String, password: String) {

        if (patientID.isBlank() || password.isBlank()) {
            _loginState.value = LoginUiState(
                error = "Please enter all fields"
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            _loginState.value = LoginUiState(error = null, success = false)

            val patient = repository.getPatientById(patientID)

            if (patient == null) {
                _loginState.value = LoginUiState(error = "No account found")
                return@launch
            }

            if (patient.password.isBlank()) {
                _loginState.value = LoginUiState(firstTime = true, error = "Please claim your account first")
                return@launch
            }

            if (patient.password != password) {
                _loginState.value = LoginUiState(error = "Incorrect password")
                return@launch
            }

            _loginState.value = LoginUiState(success = true)
        }
    }

    // Generates a unique patient ID using UUID
    fun generatePatientId(): String {
        return "P" + UUID.randomUUID().toString().take(5)
    }

    private val _signUpState = MutableStateFlow(SignUpUiState())
    val signUpState: StateFlow<SignUpUiState> =
        _signUpState.asStateFlow()

    // Validates sign-up inputs and registers a new patient if valid
    fun validateAndRegister(
        name: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {

        val state = SignUpUiState(
            nameError = name.isBlank(),

            phoneEmptyError = phone.isBlank(),
            phoneFormatError = !(phone.startsWith("04") && phone.length == 10),

            passwordEmptyError = password.isBlank(),
            passwordLengthError = password.length < 8,
            passwordLetterError = !password.any { it.isLetter() },
            passwordDigitError = !password.any { it.isDigit() },

            confirmPasswordError = password != confirmPassword,
            confirmPasswordEmptyError = confirmPassword.isBlank()
        )

        val hasError =
            state.nameError ||
                    state.phoneEmptyError ||
                    state.phoneFormatError ||
                    state.passwordEmptyError ||
                    state.passwordLengthError ||
                    state.passwordLetterError ||
                    state.passwordDigitError ||
                    state.confirmPasswordError ||
                    state.confirmPasswordEmptyError

        if (hasError) {
            _signUpState.value = state.copy(
                success = false,
                generalError = "Please fix validation errors"
            )
            return
        }

        viewModelScope.launch {

            val existing = repository.getPatientByPhone(phone)

            if (existing != null) {
                _signUpState.value = state.copy(
                    success = false,
                    generalError = "Phone number already exists"
                )
                return@launch
            }

            val patient = Patient(
                patientID = generatePatientId(),
                phoneNumber = phone,
                name = name,
                password = password
            )

            repository.insertPatient(patient)

            _signUpState.value = state.copy(
                success = true,
                generalError = null
            )
        }

    }

    private val _claimState = MutableStateFlow(ClaimUiState())
    val claimState: StateFlow<ClaimUiState> = _claimState.asStateFlow()

    // Allows patient to claim account by verifying details and setting password
    fun claimAccount(
        patientID: String,
        phoneNumber: String,
        newPassword: String
    ) {

        if (patientID.isBlank() || phoneNumber.isBlank() || newPassword.isBlank()) {
            _claimState.value = ClaimUiState(
                generalError = "Please fill in all fields"
            )
            return
        }

        viewModelScope.launch {

            val patient = repository.getPatientById(patientID)

            val state = when {

                patient == null -> ClaimUiState(
                    patientIdError = true,
                    generalError = "Invalid Patient ID"
                )

                patient.phoneNumber != phoneNumber -> ClaimUiState(
                    phoneError = true,
                    generalError = "Phone number does not match"
                )

                !patient.password.isNullOrEmpty() -> ClaimUiState(
                    alreadyClaimedError = true,
                    generalError = "Account already claimed"
                )

                else -> {
                    repository.updatePassword(patientID, newPassword)

                    ClaimUiState(
                        success = true,
                        generalError = null
                    )
                }
            }

            _claimState.value = state
        }
    }

    // Retrieves patient name
    fun getPatientName(patientID: String?): Flow<String?> {
        return repository.getPatientName(patientID)
    }

    // Retrieves patient phone number
    fun getPatientPhoneNumber(patientID: String?): Flow<String?> {
        return repository.getPatientPhoneNumber(patientID)
    }

    fun getCurrentPatientId(context: Context): String {
        val prefs = context.getSharedPreferences("logged_in_patient_id", Context.MODE_PRIVATE)
        return prefs.getString("patient_id", "") ?: ""
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences("logged_in_patient_id", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }


    // Factory
    class PatientsViewModelFactory( private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientsViewModel(application) as T
        }

    }


}