package com.example

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DiagnosticRunner {
    private const val TAG = "SHOONYA_DIAG"

    fun runDiagnostics() {
        CoroutineScope(Dispatchers.IO).launch {
            val uid = try { BuildConfig.SHOONYA_USER_ID } catch (e: Exception) { "" }
            if (uid.isBlank() || uid == "MY_SHOONYA_USER_ID" || uid == "YOUR_SHOONYA_USER_ID") {
                Log.i(TAG, "Shoonya credentials not configured; running in simulated live stream mode.")
                return@launch
            }

            Log.d(TAG, "--- STEP 1: Session Check ---")
            var isLoggedIn = ShoonyaApiService.sessionToken != null
            if (!isLoggedIn) {
                Log.d(TAG, "Attempting Shoonya login...")
                isLoggedIn = ShoonyaApiService.login()
                Log.d(TAG, "Login attempt result: $isLoggedIn")
            }

            if (!isLoggedIn) {
                Log.i(TAG, "Shoonya login skipped or failed. Verify your credentials in Settings.")
                return@launch
            }

            Log.d(TAG, "--- STEP 2 & 3: Symbol & Token Extraction Check ---")
            val searchResults = ShoonyaApiService.searchScrip("BANKNIFTY 52000", "NFO")
            Log.d(TAG, "Search Result Token: $searchResults")
            
            if (searchResults != null) {
                Log.d(TAG, "--- STEP 4: Live Quote Data Check ---")
                val quote = ShoonyaApiService.getQuote("NFO", searchResults)
                Log.d(TAG, "Quote result (LTP): $quote")
            } else {
                Log.i(TAG, "Step 2/3: Search did not return a token.")
            }
        }
    }
}

