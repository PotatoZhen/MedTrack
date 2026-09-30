package com.wan.s34476474.medtrack.utils

import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import java.util.Locale


/*
Creates a TimePickerDialog and updates the provided state with selected time.
 */
@Composable
fun timePickerFun(time: MutableState<String>): TimePickerDialog {
    val context = LocalContext.current

    val calendar = Calendar.getInstance()

    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    calendar.time = Calendar.getInstance().time

    return TimePickerDialog(
        context,
        { _, hour, minute: Int ->
            time.value = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }, hour, minute, false
    )
}
