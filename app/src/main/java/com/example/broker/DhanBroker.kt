package com.example.broker

import com.example.data.model.MarketData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*

class DhanBroker(private val client: OkHttpClient) : Broker, WebSocketListener() {

    private val clientId: String = try { com.example.BuildConfig.DHAN_CLIENT_ID } catch (e: Exception) { "YOUR_CLIENT_ID" }
    private val accessToken: String = try { com.example.BuildConfig.DHAN_ACCESS_TOKEN } catch (e: Exception) { "YOUR_ACCESS_TOKEN" }
    private var webSocket: WebSocket? = null
    private val _marketDataFlow = MutableStateFlow<Map<String, MarketData>>(emptyMap())
    override val marketDataFlow: StateFlow<Map<String, MarketData>> = _marketDataFlow.asStateFlow()

    override fun connect() {
        val request = Request.Builder()
            .url("wss://api-feed.dhan.co")
            .build()
        webSocket = client.newWebSocket(request, this)
    }

    override fun subscribe(symbols: List<String>) {
        val symbolsStr = symbols.joinToString(prefix = "[", postfix = "]", separator = ",") { "\"$it\"" }
        val subscribeMessage = """{"action":"subscribe","symbols":$symbolsStr}"""
        webSocket?.send(subscribeMessage)
    }

    override fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    override suspend fun buy(symbol: String, quantity: Int, price: Double) {
        // Dhan API buy order placement logic placeholder
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        val authMessage = """{"action":"auth","access_token":"$accessToken","client_id":"$clientId"}"""
        webSocket.send(authMessage)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // Dhan real-time tick message parser
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.printStackTrace()
    }
}
