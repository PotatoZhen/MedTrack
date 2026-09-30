package com.wan.s34476474.medtrack.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wan.s34476474.medtrack.MedicationActivity
import com.wan.s34476474.medtrack.SignUpActivity
import com.wan.s34476474.medtrack.data.medications.Medication
import com.wan.s34476474.medtrack.data.patients.Patient

object sharedPreferencesReader {

    fun loadAccount(context: Context): MutableList<Patient> {

        val sharedPreferences = context.getSharedPreferences(
            "users",
            Context.MODE_PRIVATE
        )


        val gson = Gson()
        val json = sharedPreferences.getString("patient_list", null)
            ?: return mutableListOf()

        val type = object : TypeToken<MutableList<Patient>>() {}.type
        return gson.fromJson(json, type)
    }

    fun loadMedication(context: Context): MutableList<Medication> {

        val sharedPreferences = context.getSharedPreferences(
            "medications",
            Context.MODE_PRIVATE
        )

        val gson = Gson()
        val json = sharedPreferences.getString("medication_list", null)
            ?: return mutableListOf()

        val type = object : TypeToken<MutableList<Medication>>() {}.type
        return gson.fromJson(json, type)
    }
}