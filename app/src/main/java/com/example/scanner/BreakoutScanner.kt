package com.example.scanner

import com.example.data.model.MarketData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class BreakoutType {
    INTRADAY, WEEKLY, BTST
}

data class BreakoutSignal(
    val symbol: String,
    val type: BreakoutType,
    val price: Double,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

class BreakoutScanner {
    private val fiftyTwoWeekHighs = mutableMapOf<String, Double>()
    private val averageVolumes = mutableMapOf<String, Long>()

    fun scan(marketDataFlow: Flow<Map<String, MarketData>>): Flow<List<BreakoutSignal>> {
        return marketDataFlow.map { dataMap ->
            val signals = mutableListOf<BreakoutSignal>()
            dataMap.forEach { (symbol, data) ->
                // 1. 52-Week High Breakout
                fiftyTwoWeekHighs[symbol]?.let { high52 ->
                    if (data.price > high52) {
                        signals.add(
                            BreakoutSignal(
                                symbol = symbol,
                                type = BreakoutType.WEEKLY,
                                price = data.price,
                                reason = "Price crossed 52-week high ($high52)"
                            )
                        )
                    }
                }
                // 2. Volume Spike Breakout
                averageVolumes[symbol]?.let { avgVol ->
                    if (data.volume > avgVol * 2) {
                        signals.add(
                            BreakoutSignal(
                                symbol = symbol,
                                type = BreakoutType.INTRADAY,
                                price = data.price,
                                reason = "Volume (${data.volume}) is > 2x average ($avgVol)"
                            )
                        )
                    }
                }
                // 3. BTST (Buy Today Sell Tomorrow) - Momentum based
                if (data.changePercentage > 3.0) {
                    signals.add(
                        BreakoutSignal(
                            symbol = symbol,
                            type = BreakoutType.BTST,
                            price = data.price,
                            reason = "Strong upward momentum (${"%.2f".format(data.changePercentage)}%)"
                        )
                    )
                }
            }
            signals
        }
    }

    fun updateHistoricalData(highs: Map<String, Double>, volumes: Map<String, Long>) {
        fiftyTwoWeekHighs.putAll(highs)
        averageVolumes.putAll(volumes)
    }
}
