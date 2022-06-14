package com.ictis.neos.core

import org.koin.dsl.module
import okhttp3.OkHttpClient

import com.ictis.neos.auth.AuthService
import com.ictis.neos.camera.CameraService
import com.ictis.neos.clients.NeosClient
import com.ictis.neos.recognition.RecognitionService
import com.ictis.neos.training.TrainingService


val coreModule = module {
    single { FileService() }
    single { SharedPreferencesService() }
    single { GenericUIService() }
}

val authModule = module {
    single { AuthService() }
}

val apiModule = module {
    single { OkHttpClient() }
    single { NeosClient() }
}

val cameraModule = module {
    single { CameraService() }
}

val recognitionModule = module {
    single { RecognitionService() }
}

val trainingModule = module {
    single { TrainingService() }
}
