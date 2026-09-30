package com.wan.s34476474.medtrack.data.patients

// Holds sign-up screen UI states
data class SignUpUiState(
    val nameError: Boolean = false,
    val phoneEmptyError: Boolean = false,
    val phoneFormatError: Boolean = false,
    val passwordEmptyError: Boolean = false,
    val passwordLengthError: Boolean = false,
    val passwordLetterError: Boolean = false,
    val passwordDigitError: Boolean = false,
    val confirmPasswordError: Boolean = false,
    val confirmPasswordEmptyError: Boolean = false,
    val generalError: String? = null,
    val success: Boolean = false
)
