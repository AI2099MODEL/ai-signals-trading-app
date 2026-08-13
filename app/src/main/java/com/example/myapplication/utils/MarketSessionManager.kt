package com.example.myapplication.utils

import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.DayOfWeek

object MarketSessionManager {
    private val IST_ZONE = ZoneId.of("Asia/Kolkata")

    enum class MarketSegment {
        EQUITY,
        MCX
    }

    /**
     * Checks if the market is currently open for the given segment in IST.
     */
    fun isMarketOpen(segment: MarketSegment): Boolean {
        val now = ZonedDateTime.now(IST_ZONE)
        val dayOfWeek = now.dayOfWeek
        
        // Markets are closed on weekends
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false
        }

        val currentTime = now.toLocalTime()

        return when (segment) {
            MarketSegment.EQUITY -> {
                val openTime = LocalTime.of(9, 15)
                val closeTime = LocalTime.of(15, 30)
                currentTime.isAfter(openTime) && currentTime.isBefore(closeTime)
            }
            MarketSegment.MCX -> {
                val openTime = LocalTime.of(9, 0)
                val closeTime = LocalTime.of(23, 30) // Simplified, usually 23:30 or 23:50
                currentTime.isAfter(openTime) && currentTime.isBefore(closeTime)
            }
        }
    }

    /**
     * Returns a descriptive status of the market.
     */
    fun getMarketStatus(segment: MarketSegment): String {
        return if (isMarketOpen(segment)) "Open" else "Closed"
    }
}
