package com.example.myapplication.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ShoonyaLoginRequest(
    val apkversion: String = "1.0.0",
    val uid: String,
    val pwd: String,
    val factor2: String,
    val vc: String,
    val appkey: String,
    val source: String = "API"
)

@Serializable
data class ShoonyaLoginResponse(
    val stat: String,
    val susertoken: String? = null,
    val lasttime: String? = null,
    val uname: String? = null,
    val actid: String? = null,
    val email: String? = null,
    val brkname: String? = null,
    val emsg: String? = null
)

@Serializable
data class ShoonyaWebSocketRequest(
    val t: String, // 'c' for connect, 'h' for touchline, etc.
    val uid: String? = null,
    val actid: String? = null,
    val susertoken: String? = null,
    val source: String? = "API",
    val tkey: String? = null,
    val k: String? = null // subscription keys like "NSE|22"
)

@Serializable
data class ShoonyaQuoteResponse(
    val t: String? = null, // 'tk' for touchline
    val e: String? = null, // Exchange
    val tk: String? = null, // Token
    val lp: String? = null, // Last Price
    val pc: String? = null, // Percentage Change
    val v: String? = null, // Volume
    val s: String? = null // Status for connection ('OK')
)
