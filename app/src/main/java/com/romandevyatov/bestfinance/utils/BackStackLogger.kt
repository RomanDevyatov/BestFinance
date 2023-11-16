package com.romandevyatov.bestfinance.utils

import android.util.Log
import androidx.navigation.NavController

object BackStackLogger {
    fun logBackStack(navController: NavController) {
        val backStack = navController.backQueue
        Log.d("BackStack", "Started:")
        for (entry in backStack) {
            Log.d("BackStack", "Fragment: ${entry.destination.label}")
        }
    }
}
