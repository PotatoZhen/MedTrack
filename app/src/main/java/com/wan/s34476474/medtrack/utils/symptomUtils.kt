package com.wan.s34476474.medtrack.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * Opens Date + Time picker and stores formatted result.
 */
fun dateTimePickerFun(dateTime: MutableState<String>, context: Context) {

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->

            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            // open time picker after date
            TimePickerDialog(
                context,
                { _, hour, minute ->

                    selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                    selectedDate.set(Calendar.MINUTE, minute)

                    dateTime.value = SimpleDateFormat(
                        "d/M/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(selectedDate.time)

                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()

        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.show()
}

/**
 * Converts severity number into label.
 */
fun getSeverityLabel(value: Int): String {
    return when (value) {
        in 1..3 -> "Mild"
        in 4..6 -> "Moderate"
        in 7..10 -> "Severe"
        else -> "Unknown"
    }
}

/**
 * Returns severity color based on value.
 */
fun getSeverityColor(value: Int): Color {
    return when (value) {
        in 1..3 -> Color(0xFF4CAF50)   // Green
        in 4..6 -> Color(0xFFFFC107)   // Amber
        in 7..10 -> Color(0xFFF44336)  // Red
        else -> Color.Gray
    }
}