package com.example.engine

import com.example.data.model.MarketData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Position(
    val symbol: String,
    val quantity: Int,
    val averagePrice: Double,
    val currentPrice: Double
) {
    val pnl: Double get() = (currentPrice - averagePrice) * quantity
    val pnlPercentage: Double get() = if (averagePrice != 0.0) ((currentPrice - averagePrice) / averagePrice) * 100 else 0.0
}

data class Trade(
    val symbol: String,
    val quantity: Int,
    val price: Double,
    val type: TradeType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TradeType {
    BUY, SELL
}

class VirtualTradingEngine(initialBalance: Double = 100000.0) {
    private val _balance = MutableStateFlow(initialBalance)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _positions = MutableStateFlow<Map<String, Position>>(emptyMap())
    val positions: StateFlow<Map<String, Position>> = _positions.asStateFlow()

    private val _tradeHistory = MutableStateFlow<List<Trade>>(emptyList())
    val tradeHistory: StateFlow<List<Trade>> = _tradeHistory.asStateFlow()

    fun buy(symbol: String, quantity: Int, price: Double) {
        val cost = quantity * price
        if (_balance.value >= cost) {
            _balance.value -= cost
            val currentPositions = _positions.value.toMutableMap()
            val existing = currentPositions[symbol]
            if (existing != null) {
                val totalQty = existing.quantity + quantity
                val newAvg = (existing.averagePrice * existing.quantity + cost) / totalQty
                currentPositions[symbol] = existing.copy(
                    quantity = totalQty,
                    averagePrice = newAvg,
                    currentPrice = price
                )
            } else {
                currentPositions[symbol] = Position(symbol, quantity, price, price)
            }
            _positions.value = currentPositions
            _tradeHistory.value = _tradeHistory.value + Trade(symbol, quantity, price, TradeType.BUY)
        }
    }

    fun sell(symbol: String, quantity: Int, price: Double) {
        val currentPositions = _positions.value.toMutableMap()
        val existing = currentPositions[symbol]
        if (existing != null && existing.quantity >= quantity) {
            val proceeds = quantity * price
            _balance.value += proceeds
            if (existing.quantity == quantity) {
                currentPositions.remove(symbol)
            } else {
                currentPositions[symbol] = existing.copy(
                    quantity = existing.quantity - quantity,
                    currentPrice = price
                )
            }
            _positions.value = currentPositions
            _tradeHistory.value = _tradeHistory.value + Trade(symbol, quantity, price, TradeType.SELL)
        }
    }

    fun updateMarketPrices(marketData: Map<String, MarketData>) {
        val currentPositions = _positions.value.toMutableMap()
        var changed = false
        currentPositions.forEach { (symbol, position) ->
            marketData[symbol]?.let { data ->
                if (position.currentPrice != data.price) {
                    currentPositions[symbol] = position.copy(currentPrice = data.price)
                    changed = true
                }
            }
        }
        if (changed) {
            _positions.value = currentPositions
        }
    }
}
