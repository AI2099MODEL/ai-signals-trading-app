package com.example.broker

import com.example.data.model.MarketData
import kotlinx.coroutines.flow.StateFlow

interface Broker {
    val marketDataFlow: StateFlow<Map<String, MarketData>>
    fun connect()
    fun subscribe(symbols: List<String>)
    fun disconnect()
    suspend fun buy(symbol: String, quantity: Int, price: Double)
}
