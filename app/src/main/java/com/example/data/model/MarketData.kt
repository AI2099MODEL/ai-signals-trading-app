package com.example.data.model

data class MarketData(
    val symbol: String,
    val price: Double,
    val change: Double = 0.0,
    val changePercentage: Double = 0.0,
    val volume: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
