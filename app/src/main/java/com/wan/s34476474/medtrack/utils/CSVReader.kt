package com.wan.s34476474.medtrack.utils

import android.content.Context
import com.wan.s34476474.medtrack.data.patients.Patient
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.sequences.forEach

object CSVReader {

    fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false

        for (c in line) {
            when (c) {
                '"' -> inQuotes = !inQuotes
                ',' -> {
                    if (inQuotes) {
                        current.append(c)
                    } else {
                        result.add(current.toString().trim())
                        current.clear()
                    }
                }
                else -> current.append(c)
            }
        }

        result.add(current.toString().trim())
        return result
    }

    fun <T> readCSV(
        context: Context,
        fileName: String,
        mapper: (List<String>) -> T
    ): List<T> {

        val list = mutableListOf<T>()

        context.assets.open(fileName).bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line ->
                val values = parseCsvLine(line)
                list.add(mapper(values))
            }
        }

        return list
    }

}

