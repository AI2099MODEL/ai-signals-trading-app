package com.example.myapplication

/**
 * Utility to access API secrets securely.
 * The values are injected via BuildConfig during build time.
 */
object SecretsConfig {
    val shoonyaSecret: String
        get() = BuildConfig.SHOONYA_SECRET

    val dhanSecret: String
        get() = BuildConfig.DHAN_SECRET

    /**
     * Checks if secrets are properly configured.
     */
    fun areSecretsLoaded(): Boolean {
        return shoonyaSecret.isNotEmpty() && dhanSecret.isNotEmpty()
    }
}
