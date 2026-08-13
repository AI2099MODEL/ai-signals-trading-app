package com.example

import android.content.Context
import android.util.Log
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class SupabaseTradeDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "ticker") val ticker: String,
    @Json(name = "name") val name: String,
    @Json(name = "entry_price") val entryPrice: Double,
    @Json(name = "current_price") val currentPrice: Double,
    @Json(name = "entry_time") val entryTime: Long,
    @Json(name = "status") val status: String,
    @Json(name = "target_price") val targetPrice: Double,
    @Json(name = "trailing_sl_threshold") val trailingSLThreshold: Double,
    @Json(name = "stop_loss") val stopLoss: Double,
    @Json(name = "exit_price") val exitPrice: Double? = null,
    @Json(name = "exit_time") val exitTime: Long? = null,
    @Json(name = "highest_price") val highestPrice: Double,
    @Json(name = "profit_percent") val profitPercent: Double,
    @Json(name = "profit_amount") val profitAmount: Double,
    @Json(name = "is_partial_booked") val isPartialBooked: Boolean,
    @Json(name = "allocated_amount") val allocatedAmount: Double,
    @Json(name = "is_btst") val isBtst: Boolean,
    @Json(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class SupabaseLogDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "message") val message: String,
    @Json(name = "source") val source: String = "Engine"
)

@JsonClass(generateAdapter = true)
data class SupabaseBreakoutDto(
    @Json(name = "ticker") val ticker: String,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Double,
    @Json(name = "strategies") val strategies: String,
    @Json(name = "score") val score: Int,
    @Json(name = "reasons") val reasons: String,
    @Json(name = "signalStrength") val signalStrength: String,
    @Json(name = "stopLoss") val stopLoss: Double?,
    @Json(name = "target1") val target1: Double?,
    @Json(name = "target2") val target2: Double?,
    @Json(name = "previousClose") val previousClose: Double?,
    @Json(name = "openPrice") val openPrice: Double?,
    @Json(name = "change") val change: Double,
    @Json(name = "changePercent") val changePercent: Double,
    @Json(name = "isBtst") val isBtst: Boolean,
    @Json(name = "assetType") val assetType: String,
    @Json(name = "categoryGroup") val categoryGroup: String? = "Top 5 Breakout Stocks",
    @Json(name = "scannedAt") val scannedAt: Long
)

fun VirtualTrade.toSupabaseDto(): SupabaseTradeDto {
    return SupabaseTradeDto(
        id = if (id > 0) id else null,
        ticker = ticker,
        name = name,
        entryPrice = entryPrice,
        currentPrice = currentPrice,
        entryTime = entryTime,
        status = status,
        targetPrice = targetPrice,
        trailingSLThreshold = trailingSLThreshold,
        stopLoss = stopLoss,
        exitPrice = exitPrice,
        exitTime = exitTime,
        highestPrice = highestPrice,
        profitPercent = profitPercent,
        profitAmount = profitAmount,
        isPartialBooked = isPartialBooked,
        allocatedAmount = allocatedAmount,
        isBtst = isBtst,
        updatedAt = System.currentTimeMillis()
    )
}

fun SupabaseTradeDto.toVirtualTrade(): VirtualTrade {
    return VirtualTrade(
        id = id ?: 0,
        ticker = ticker,
        name = name,
        entryPrice = entryPrice,
        currentPrice = currentPrice,
        entryTime = entryTime,
        status = status,
        targetPrice = targetPrice,
        trailingSLThreshold = trailingSLThreshold,
        stopLoss = stopLoss,
        exitPrice = exitPrice,
        exitTime = exitTime,
        highestPrice = highestPrice,
        profitPercent = profitPercent,
        profitAmount = profitAmount,
        isPartialBooked = isPartialBooked,
        allocatedAmount = allocatedAmount,
        isBtst = isBtst
    )
}

fun ScannedBreakout.toSupabaseDto(): SupabaseBreakoutDto {
    return SupabaseBreakoutDto(
        ticker = ticker,
        name = name,
        price = price,
        strategies = strategies,
        score = score,
        reasons = reasons,
        signalStrength = signalStrength,
        stopLoss = stopLoss,
        target1 = target1,
        target2 = target2,
        previousClose = previousClose,
        openPrice = openPrice,
        change = change,
        changePercent = changePercent,
        isBtst = isBtst,
        assetType = assetType,
        categoryGroup = categoryGroup,
        scannedAt = scannedAt
    )
}

fun SupabaseBreakoutDto.toScannedBreakout(): ScannedBreakout {
    return ScannedBreakout(
        ticker = ticker,
        name = name,
        price = price,
        strategies = strategies,
        score = score,
        reasons = reasons,
        signalStrength = signalStrength,
        stopLoss = stopLoss,
        target1 = target1,
        target2 = target2,
        previousClose = previousClose,
        openPrice = openPrice,
        change = change,
        changePercent = changePercent,
        isBtst = isBtst,
        assetType = assetType,
        categoryGroup = categoryGroup ?: "Top 5 Breakout Stocks",
        scannedAt = scannedAt
    )
}

interface SupabaseApi {
    @GET("rest/v1/virtual_trades")
    suspend fun getTrades(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "entry_time.desc"
    ): Response<List<SupabaseTradeDto>>

    @POST("rest/v1/virtual_trades")
    @Headers("Prefer: return=representation, resolution=merge-duplicates")
    suspend fun upsertTrade(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body trade: SupabaseTradeDto
    ): Response<List<SupabaseTradeDto>>

    @POST("rest/v1/virtual_trades")
    @Headers("Prefer: return=representation, resolution=merge-duplicates")
    suspend fun upsertTrades(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body trades: List<SupabaseTradeDto>
    ): Response<List<SupabaseTradeDto>>

    @DELETE("rest/v1/virtual_trades")
    suspend fun deleteAllTrades(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("id") filter: String = "gt.0"
    ): Response<Unit>

    @GET("rest/v1/scanned_breakouts")
    suspend fun getBreakouts(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "score.desc"
    ): Response<List<SupabaseBreakoutDto>>

    @POST("rest/v1/scanned_breakouts")
    @Headers("Prefer: return=representation, resolution=merge-duplicates")
    suspend fun upsertBreakouts(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body breakouts: List<SupabaseBreakoutDto>
    ): Response<List<SupabaseBreakoutDto>>

    @DELETE("rest/v1/scanned_breakouts")
    suspend fun deleteAllBreakouts(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("ticker") filter: String = "neq.EMPTY"
    ): Response<Unit>

    @POST("rest/v1/engine_logs")
    @Headers("Prefer: return=minimal")
    suspend fun postLog(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body log: SupabaseLogDto
    ): Response<Unit>

    @GET("rest/v1/engine_logs")
    suspend fun getLogs(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "timestamp.desc",
        @Query("limit") limit: Int = 50
    ): Response<List<SupabaseLogDto>>
}

object SupabaseSyncManager {
    private const val TAG = "SupabaseSyncManager"

    // Configurable Supabase Endpoint & Key (Default public free Cloud PostgREST tier)
    var supabaseUrl: String = "https://aistudiostockapp.supabase.co"
    var supabaseAnonKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFpc3R1ZGlvc3RvY2thcHAiLCJyb2xlIjoiYW5vbiIsImlhdCI6MTY3MjI0OTYwMCwiZXhwIjoyMDE3ODI3NjAwfQ.default_key_placeholder"

    val isCloudConnected = MutableStateFlow(false)
    val cloudStatusMessage = MutableStateFlow("Initializing Cloud Sync...")
    val lastSyncTimestamp = MutableStateFlow(0L)

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private var apiService: SupabaseApi? = null
    private var syncJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initialize(context: Context, customUrl: String? = null, customKey: String? = null) {
        val prefs = context.getSharedPreferences("supabase_config", Context.MODE_PRIVATE)
        val envUrl = try { BuildConfig.SUPABASE_URL } catch (e: Exception) { "" }
        val envKey = try { BuildConfig.SUPABASE_KEY } catch (e: Exception) { "" }
        val finalEnvUrl = if (!envUrl.isNullOrBlank() && envUrl != "MY_SUPABASE_URL") envUrl else supabaseUrl
        val finalEnvKey = if (!envKey.isNullOrBlank() && envKey != "MY_SUPABASE_KEY") envKey else supabaseAnonKey
        val url = customUrl ?: prefs.getString("supabase_url", finalEnvUrl) ?: finalEnvUrl
        val key = customKey ?: prefs.getString("supabase_key", finalEnvKey) ?: finalEnvKey

        supabaseUrl = url.trimEnd('/')
        supabaseAnonKey = key

        if (customUrl != null || customKey != null) {
            prefs.edit()
                .putString("supabase_url", supabaseUrl)
                .putString("supabase_key", supabaseAnonKey)
                .apply()
        }

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("$supabaseUrl/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            apiService = retrofit.create(SupabaseApi::class.java)
            cloudStatusMessage.value = "Cloud Sync Ready (Supabase SQL)"
            isCloudConnected.value = true
            Log.d(TAG, "Supabase initialized with URL: $supabaseUrl")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Supabase Retrofit client", e)
            cloudStatusMessage.value = "Cloud Sync Offline (Using Room Local)"
            isCloudConnected.value = false
        }
    }

    fun startPeriodicSync() {
        syncJob?.cancel()
        syncJob = scope.launch {
            while (isActive) {
                try {
                    syncTradesWithCloud()
                    syncBreakoutsWithCloud()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in periodic cloud sync cycle", e)
                }
                delay(12_000) // Sync every 12 seconds
            }
        }
    }

    fun stopPeriodicSync() {
        syncJob?.cancel()
        syncJob = null
    }

    suspend fun syncTradesWithCloud() = withContext(Dispatchers.IO) {
        val service = apiService ?: return@withContext
        val db = MyApplication.database

        try {
            val bearer = "Bearer $supabaseAnonKey"
            val response = service.getTrades(apiKey = supabaseAnonKey, bearerToken = bearer)

            if (response.isSuccessful && response.body() != null) {
                val cloudTrades = response.body()!!
                val localTrades = db.virtualTradeDao().getAllTradesList()

                for (cloudTrade in cloudTrades) {
                    val localMatch = localTrades.find { 
                        (cloudTrade.id != null && cloudTrade.id > 0 && it.id == cloudTrade.id) ||
                        (it.ticker.equals(cloudTrade.ticker, ignoreCase = true) && Math.abs(it.entryTime - cloudTrade.entryTime) < 15000) ||
                        (it.ticker.equals(cloudTrade.ticker, ignoreCase = true) && it.status == "ACTIVE" && cloudTrade.status == "ACTIVE")
                    }
                    if (localMatch == null) {
                        db.virtualTradeDao().insertTrade(cloudTrade.toVirtualTrade().copy(id = 0))
                    } else if (cloudTrade.status != localMatch.status || Math.abs(cloudTrade.currentPrice - localMatch.currentPrice) > 0.01) {
                        db.virtualTradeDao().updateTrade(cloudTrade.toVirtualTrade().copy(id = localMatch.id))
                    }
                }

                val tradesToUpload = localTrades.map { it.toSupabaseDto() }
                if (tradesToUpload.isNotEmpty()) {
                    service.upsertTrades(apiKey = supabaseAnonKey, bearerToken = bearer, trades = tradesToUpload)
                }

                lastSyncTimestamp.value = System.currentTimeMillis()
                cloudStatusMessage.value = "Synced with Cloud PostgreSQL"
                isCloudConnected.value = true
            } else {
                cloudStatusMessage.value = "Cloud Sync Standby (Local Room Active)"
            }
        } catch (e: Exception) {
            Log.w(TAG, "Cloud sync connection notice: ${e.message}")
            cloudStatusMessage.value = "Cloud Sync Offline (Using Room Local)"
        }
    }

    suspend fun syncBreakoutsWithCloud() = withContext(Dispatchers.IO) {
        val service = apiService ?: return@withContext
        val db = MyApplication.database

        try {
            val bearer = "Bearer $supabaseAnonKey"
            val response = service.getBreakouts(apiKey = supabaseAnonKey, bearerToken = bearer)

            if (response.isSuccessful && response.body() != null) {
                val cloudBreakouts = response.body()!!.map { it.toScannedBreakout() }
                db.scannedBreakoutDao().clearAll()
                db.scannedBreakoutDao().insertBreakouts(cloudBreakouts)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Breakout sync notice: ${e.message}")
        }
    }

    fun publishBreakouts(breakouts: List<ScannedBreakout>) {
        scope.launch {
            val service = apiService ?: return@launch
            try {
                val bearer = "Bearer $supabaseAnonKey"
                service.upsertBreakouts(apiKey = supabaseAnonKey, bearerToken = bearer, breakouts = breakouts.map { it.toSupabaseDto() })
            } catch (e: Exception) {
                Log.w(TAG, "Could not push breakouts to cloud: ${e.message}")
            }
        }
    }

    fun publishTrade(trade: VirtualTrade) {
        scope.launch {
            val service = apiService ?: return@launch
            try {
                val bearer = "Bearer $supabaseAnonKey"
                service.upsertTrade(apiKey = supabaseAnonKey, bearerToken = bearer, trade = trade.toSupabaseDto())
                Log.d(TAG, "Trade published to Supabase cloud: ${trade.ticker}")
            } catch (e: Exception) {
                Log.w(TAG, "Could not push trade to cloud immediately: ${e.message}")
            }
        }
    }

    fun publishLog(message: String) {
        scope.launch {
            val service = apiService ?: return@launch
            try {
                val bearer = "Bearer $supabaseAnonKey"
                val dto = SupabaseLogDto(timestamp = System.currentTimeMillis(), message = message, source = "AutoTrader")
                service.postLog(apiKey = supabaseAnonKey, bearerToken = bearer, log = dto)
            } catch (e: Exception) {
                // Silently ignore log push failures
            }
        }
    }

    suspend fun clearAllCloudTrades() = withContext(Dispatchers.IO) {
        val service = apiService ?: return@withContext
        try {
            val bearer = "Bearer $supabaseAnonKey"
            service.deleteAllTrades(apiKey = supabaseAnonKey, bearerToken = bearer)
            Log.d(TAG, "All cloud trades cleared from Supabase")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear cloud trades", e)
        }
    }
}
