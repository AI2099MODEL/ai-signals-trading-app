package com.example

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object IndexScanner {
    private const val TAG = "IndexScanner"

    private val INDICES = listOf(
        Pair("NIFTY", "NSE"),
        Pair("BANKNIFTY", "NSE"),
        Pair("SENSEX", "BSE")
    )

    suspend fun scanIndices(): List<ScanResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<ScanResult>()
        
        Log.d(TAG, "Starting scan for ${INDICES.size} indices...")
        
        for ((name, exchange) in INDICES) {
            val tokenPair = MasterTokenRepository.getToken(name)
            if (tokenPair != null) {
                val (exchange, token) = tokenPair
                ShoonyaWebSocketManager.subscribeToToken(exchange, token) // Subscribe for live feed
                val price = ShoonyaApiService.getQuote(exchange, token) ?: 0.0
                if (price > 0.0) {
                    results.add(ScanResult(
                        ticker = name,
                        name = name,
                        price = price,
                        strategies = "Live Data",
                        score = 80,
                        reasons = "Live index data",
                        signalStrength = "NEUTRAL",
                        stopLoss = 0.0,
                        target1 = 0.0,
                        target2 = 0.0,
                        assetType = "INDEX"
                    ))
                } else {
                    Log.w(TAG, "Failed to get price for $name")
                }
            } else {
                Log.w(TAG, "Failed to find token for $name")
            }
        }
        
        if (results.isNotEmpty()) {
            Log.d(TAG, "Scan successful, results: ${results.size}. Pushing to Supabase.")
            // Use existing Supabase sync mechanism to push results
            val breakouts = results.map { 
                ScannedBreakout(
                    ticker = it.ticker,
                    name = it.name,
                    price = it.price,
                    strategies = it.strategies,
                    score = it.score,
                    reasons = it.reasons,
                    signalStrength = it.signalStrength,
                    stopLoss = it.stopLoss,
                    target1 = it.target1,
                    target2 = it.target2,
                    previousClose = 0.0,
                    openPrice = 0.0,
                    change = 0.0,
                    changePercent = 0.0,
                    isBtst = false,
                    assetType = it.assetType,
                    scannedAt = System.currentTimeMillis()
                ) 
            }
            SupabaseSyncManager.publishBreakouts(breakouts)
        } else {
            Log.e(TAG, "Scan failed to return any results. Trying local database fallback.")
            val cachedBreakouts = MyApplication.database.scannedBreakoutDao().getAllScannedBreakoutsList()
            if (cachedBreakouts.isNotEmpty()) {
                Log.d(TAG, "Using cached results from local database: ${cachedBreakouts.size}")
                results.addAll(cachedBreakouts.map { 
                    ScanResult(
                        ticker = it.ticker,
                        name = it.name,
                        price = it.price,
                        strategies = it.strategies,
                        score = it.score,
                        reasons = "Cached: " + it.reasons,
                        signalStrength = it.signalStrength,
                        stopLoss = it.stopLoss,
                        target1 = it.target1,
                        target2 = it.target2,
                        assetType = it.assetType
                    )
                })
            }
        }
        
        results
    }
}
