package com.wan.s34476474.medtrack.data.drugs

// Represents structured drug label information retrieved from the OpenFDA API
data class DrugItem(
    val purpose: List<String>?,
    val warnings: List<String>?,
    val dosage_and_administration: List<String>?
)
