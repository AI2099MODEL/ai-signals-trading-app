package com.example

object MasterTokenRepository {
    // These are the hardcoded tokens as the foundational backbone for indices.
    // In a full production implementation, this would be populated by parsing 
    // the downloaded daily master symbol files.
    private val INDEX_TOKENS = mapOf(
        "NIFTY" to Pair("NSE", "26009"),
        "BANKNIFTY" to Pair("NSE", "26037"),
        "SENSEX" to Pair("BSE", "1") // Placeholder for Sensex, need valid token
    )

    fun getToken(symbol: String): Pair<String, String>? {
        return INDEX_TOKENS[symbol]
    }
}
