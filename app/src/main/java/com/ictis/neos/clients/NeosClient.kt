package com.ictis.neos.clients

import android.util.Log
import com.beust.klaxon.Klaxon
import com.ictis.neos.auth.AuthService
import okhttp3.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.io.IOException


class NeosClient: KoinComponent {
    private val client: OkHttpClient by inject()
    private val authService: AuthService by inject()

    private inline fun <reified T>getSync(url: String) : T? {
        val apiKey = authService.getKey()!!
        val request = Request.Builder()
            .addHeader("x-api-key", apiKey)
            .url("${BASE_API_URL}$url")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return Klaxon().parse<T>(response.body()!!.string())
        }
    }

    // Global

    fun getHealth() : HealthResponse? {
        Log.d(TAG, "getHealth")
        return this.getSync("/health/")
    }

    // Accounts

    fun saveUserCharacterSign(char: String, file: File) : OperationStatusResponse? {
        Log.d(TAG, "saveUserCharacterSign: saving for $char")
        val url = "/accounts/save-user-character-sign/"
        val apiKey = authService.getKey()!!

        val mediaType = MediaType.parse("image/jpeg")
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                RequestBody.create(mediaType, file)
            )
            .addFormDataPart("character", char)
            .build()

        val request = Request.Builder()
            .addHeader("x-api-key", apiKey)
            .url("${BASE_API_URL}$url")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return Klaxon().parse<OperationStatusResponse>(response.body()!!.string())
        }
    }

    // Classifier

    fun getUserModel() : UserModelDataResponse? {
        Log.d(TAG, "getUserModel")
        return this.getSync("/classifier/get-model-training-data/")
    }

    fun trainUserModel() : OperationStatusResponse? {
        Log.d(TAG, "trainUserModel")
        val url = "/classifier/start-model-training/"
        val apiKey = authService.getKey()!!

        val requestBody = RequestBody.create(null, ByteArray(0))
        val request = Request.Builder()
            .addHeader("x-api-key", apiKey)
            .url("${BASE_API_URL}$url")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return Klaxon().parse<OperationStatusResponse>(response.body()!!.string())
        }
    }

    companion object {
        // ipconfig getifaddr en0
        const val BASE_API_URL = "http://192.168.178.52:8000/api"
        private const val TAG = "NeosClient"
    }
}
