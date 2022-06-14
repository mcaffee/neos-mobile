package com.ictis.neos.training

import java.io.FileOutputStream
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import org.koin.core.KoinComponent
import org.koin.core.inject
import com.ictis.neos.R
import com.ictis.neos.clients.NeosClient
import com.ictis.neos.core.FileService
import com.ictis.neos.core.ImageUtils
import com.ictis.neos.core.SharedPreferencesService
import java.io.File
import java.io.FileNotFoundException

class TrainingService: KoinComponent {
    private val client: NeosClient by inject()
    private val fileService: FileService by inject()
    private val sharedPreferencesService: SharedPreferencesService by inject()

    fun sendUserCharacterSign(char: String, file: File) {
//        val imageFile = ImageUtils.saveToJpgCache(bitmap)
        client.saveUserCharacterSign(char, file)
    }

    fun trainUserModel() {
        Log.d(TAG, "downloadUserModel: running")
        val response = client.trainUserModel()
        Log.d(TAG, "downloadUserModel: ${response!!.status}")
    }

    fun downloadUserModel() {
        Log.d(TAG, "downloadUserModel: running")
        val response = client.getUserModel()
        val modelData = Base64.decode(response!!.data.toByteArray(), Base64.DEFAULT)

        val outputFile = fileService.getCacheFile("model-user.tflite")
        Log.d(TAG, "downloadUserModel: saving model under ${outputFile.absolutePath}")

        val fOut = FileOutputStream(outputFile)
        fOut.write(modelData)
        fOut.flush()
        fOut.close()

        sharedPreferencesService.putString(R.string.preference_usr_model_path, outputFile.absolutePath)
    }

    fun deleteUserModel() {
        Log.d(TAG, "deleteUserModel: running")
        val outputFile = fileService.getCacheFile("model-user.tflite")
        try {
            outputFile.delete()
            sharedPreferencesService.putString(R.string.preference_usr_model_path, null)
        } catch (e: FileNotFoundException) {
        }
    }

    companion object {
        private const val TAG = "TrainingService"
    }
}