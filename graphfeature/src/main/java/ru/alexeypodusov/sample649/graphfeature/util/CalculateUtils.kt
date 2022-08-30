package ru.alexeypodusov.sample649.graphfeature.util

import kotlin.math.pow

object CalculateUtils {
    fun rowValues(maxValue: Int): List<String> {
        val digitsCount = maxValue.toString().length
        val firstDigit = maxValue.toString().substring(0, 1).toInt()

        val step =  when {
            firstDigit < 2 -> {
                if (digitsCount > 1) {
                    2 * 10.0.pow((digitsCount - 2).toDouble()).toInt()
                } else {
                    1
                }
            }
            firstDigit < 5 -> {
                if (digitsCount > 1) {
                    5 * 10.0.pow((digitsCount - 2).toDouble()).toInt()
                } else {
                    1
                }

            }
            else -> {
                10.0.pow((digitsCount - 1).toDouble()).toInt()
            }
        }

        return mutableListOf<String>().apply {
            var value = 0
            while (value <= maxValue) {
                add(value.toString())
                value += step
            }
            add(value.toString())
        }
    }
}