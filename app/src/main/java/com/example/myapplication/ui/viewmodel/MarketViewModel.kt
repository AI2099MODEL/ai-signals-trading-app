package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.SecretsConfig
import com.example.myapplication.data.websocket.ShoonyaWebSocketManager
import com.example.myapplication.data.model.ShoonyaQuoteResponse
import com.example.myapplication.data.model.ShoonyaWebSocketRequest
import com.example.myapplication.utils.MarketSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MarketViewModel : ViewModel() {
    private val client = OkHttpClient()
    private val webSocketManager = ShoonyaWebSocketManager(client)

    private val _marketStatus = MutableStateFlow("Unknown")
    val marketStatus: StateFlow<String> = _marketStatus.asStateFlow()

    private val _latestQuote = MutableStateFlow<ShoonyaQuoteResponse?>(null)
    val latestQuote: StateFlow<ShoonyaQuoteResponse?> = _latestQuote.asStateFlow()

    init {
        updateMarketStatus()
        observePriceUpdates()
    }

    private fun updateMarketStatus() {
        val equityStatus = MarketSessionManager.getMarketStatus(MarketSessionManager.MarketSegment.EQUITY)
        _marketStatus.value = "Equity: $equityStatus"
    }

    private fun observePriceUpdates() {
        viewModelScope.launch {
            webSocketManager.priceUpdates.collect { quote ->
                _latestQuote.value = quote
            }
        }
    }

    fun startStreaming() {
        if (SecretsConfig.areSecretsLoaded()) {
            webSocketManager.connect()
            // In a real app, we would login first via REST API to get the susertoken.
            // For now, we'll try to authenticate if we had the token.
            // val auth = ShoonyaWebSocketRequest(t = "c", uid = "...", ...)
            // webSocketManager.authenticate(auth)
        }
    }

    fun subscribeToToken(token: String) {
        webSocketManager.subscribe(listOf(token))
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}
