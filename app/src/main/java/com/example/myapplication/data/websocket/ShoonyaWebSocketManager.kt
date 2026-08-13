package com.example.myapplication.data.websocket

import android.util.Log
import com.example.myapplication.data.model.ShoonyaQuoteResponse
import com.example.myapplication.data.model.ShoonyaWebSocketRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.util.concurrent.TimeUnit

class ShoonyaWebSocketManager(
    private val client: OkHttpClient,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    private var webSocket: WebSocket? = null
    private val _priceUpdates = MutableSharedFlow<ShoonyaQuoteResponse>(extraBufferCapacity = 100)
    val priceUpdates: SharedFlow<ShoonyaQuoteResponse> = _priceUpdates

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isConnected = false
    private var lastAuthRequest: ShoonyaWebSocketRequest? = null

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("ShoonyaWS", "WebSocket Opened")
            isConnected = true
            // Re-authenticate if we have saved credentials
            lastAuthRequest?.let { authenticate(it) }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("ShoonyaWS", "Received: $text")
            try {
                val quote = json.decodeFromString<ShoonyaQuoteResponse>(text)
                if (quote.t == "ck" && quote.s == "OK") {
                    Log.d("ShoonyaWS", "Authentication Successful")
                } else if (quote.t == "tk") {
                    scope.launch {
                        _priceUpdates.emit(quote)
                    }
                }
            } catch (e: Exception) {
                Log.e("ShoonyaWS", "Error parsing message: ${e.message}")
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("ShoonyaWS", "WebSocket Closing: $reason")
            isConnected = false
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("ShoonyaWS", "WebSocket Failure: ${t.message}")
            isConnected = false
            reconnect()
        }
    }

    fun connect() {
        val request = Request.Builder()
            .url("wss://api.shoonya.com/NorenWSTP/")
            .build()
        webSocket = client.newWebSocket(request, listener)
    }

    fun authenticate(authRequest: ShoonyaWebSocketRequest) {
        lastAuthRequest = authRequest
        val jsonMsg = json.encodeToString(authRequest)
        webSocket?.send(jsonMsg)
    }

    fun subscribe(tokens: List<String>) {
        // Shoonya subscription format: {"t":"h", "k":"NSE|22#NSE|25"}
        val key = tokens.joinToString("#")
        val subRequest = ShoonyaWebSocketRequest(t = "h", k = key)
        webSocket?.send(json.encodeToString(subRequest))
    }

    private fun reconnect() {
        scope.launch {
            delay(5000) // Simple 5s delay before reconnect
            Log.d("ShoonyaWS", "Attempting to reconnect...")
            connect()
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Normal Closure")
        isConnected = false
    }
}
