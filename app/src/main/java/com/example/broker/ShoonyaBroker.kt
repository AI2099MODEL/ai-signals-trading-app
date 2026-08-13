package com.example.broker

import com.example.ShoonyaApiService
import com.example.data.model.MarketData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import org.json.JSONObject

class ShoonyaBroker(private val client: OkHttpClient) : Broker, WebSocketListener() {

    private var webSocket: WebSocket? = null
    private val _marketDataFlow = MutableStateFlow<Map<String, MarketData>>(emptyMap())
    override val marketDataFlow: StateFlow<Map<String, MarketData>> = _marketDataFlow.asStateFlow()

    override fun connect() {
        val request = Request.Builder()
            .url("wss://api.shoonya.com/NorenWSTP/")
            .build()
        webSocket = client.newWebSocket(request, this)
    }

    override fun subscribe(symbols: List<String>) {
        val symbolsStr = symbols.joinToString("#")
        val subscribeMessage = """{"t":"t","k":"$symbolsStr"}"""
        webSocket?.send(subscribeMessage)
    }

    override fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    override suspend fun buy(symbol: String, quantity: Int, price: Double) {
        ShoonyaApiService.placeOrder(
            tradingSymbol = symbol,
            exchange = "MCX",
            transactionType = "B",
            quantity = quantity,
            price = price,
            orderType = if (price > 0.0) "LMT" else "MKT"
        )
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        val userId = try { com.example.BuildConfig.SHOONYA_USER_ID } catch (e: Exception) { "YOUR_USER_ID" }
        val sessionToken = ShoonyaApiService.sessionToken ?: "YOUR_SESSION_TOKEN"
        val authMessage = """{"t":"c","uid":"$userId","actid":"$userId","susertoken":"$sessionToken","source":"API"}"""
        webSocket.send(authMessage)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val json = JSONObject(text)
            val type = json.optString("t")
            if (type == "tk" || type == "tf") {
                val symbol = json.optString("tk")
                val price = json.optString("lp").toDoubleOrNull() ?: 0.0
                val change = json.optString("pc").toDoubleOrNull() ?: 0.0

                val currentData = _marketDataFlow.value.toMutableMap()
                currentData[symbol] = MarketData(
                    symbol = symbol,
                    price = price,
                    change = change
                )
                _marketDataFlow.value = currentData
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.printStackTrace()
    }
}
