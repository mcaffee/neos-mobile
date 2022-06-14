package com.ictis.neos.auth

import java.io.IOException
import android.util.Log
import org.koin.core.KoinComponent
import org.koin.core.inject
import com.ictis.neos.R
import com.ictis.neos.clients.NeosClient
import com.ictis.neos.core.SharedPreferencesService

class AuthService : KoinComponent {
    private val client: NeosClient by inject()
    private val sharedPreferencesService: SharedPreferencesService by inject()

    fun storeKey(key: String) {
        Log.d(TAG, "storeKey: saving the API key")
        sharedPreferencesService.putString(R.string.preference_api_key, key)
    }

    fun getKey(): String? {
        return sharedPreferencesService.getString(R.string.preference_api_key)
    }

    fun checkAuth(): Boolean {
        Log.d(TAG, "checkAuth: checking API key and trying authentication")

        if (getKey() == null) {
            Log.d(TAG, "checkAuth: API key not set")
            return false
        }

        return pingApi()
    }

    fun pingApi(): Boolean {
        return try {
            client.getHealth()
            Log.d(TAG, "pingApi: API ping successful")
            true
        } catch (e: IOException) {
            Log.e(TAG, "pingApi: API ping failed. Wrong API key?", e)
            false
        }
    }

    companion object {
        private const val TAG = "AuthService"
    }
}