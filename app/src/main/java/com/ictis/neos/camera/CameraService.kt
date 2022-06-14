package com.ictis.neos.camera

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import org.koin.core.KoinComponent
import com.ictis.neos.R
import com.ictis.neos.core.FileService
import org.koin.core.inject
import java.io.File

class CameraService : KoinComponent {
    private val fileService: FileService by inject()

    lateinit var imageCapture: ImageCapture
    lateinit var videoCapture: VideoCapture
    lateinit var preview: Preview

    private lateinit var context: Context
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK


    fun init(context: Context, lifecycleOwner: LifecycleOwner, bindCallback: () -> Unit) {
        if (context.getString(R.string.build_type) == "RELEASE") {
            // NOTE: The virtual scene only supports back camera! release version uses the front camera
            lensFacing = CameraSelector.LENS_FACING_FRONT
        }
        this.context = context
        cameraProviderFuture = ProcessCameraProvider.getInstance(this.context)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                bind(cameraProvider, lifecycleOwner, bindCallback)
            },
            ContextCompat.getMainExecutor(this.context)
        )
    }

    @SuppressLint("RestrictedApi")
    private fun bind(
        cameraProvider: ProcessCameraProvider,
        lifecycleOwner: LifecycleOwner,
        bindCallback: () -> Unit,
    ) {
        preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()
        videoCapture = VideoCapture.Builder().build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
            videoCapture,
        )

        bindCallback()
    }

    fun takePicture(
        onSuccess: (imageProxy: ImageProxy) -> Unit,
        onFailure: (exception: ImageCaptureException) -> Unit,
    ) {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    onSuccess(imageProxy)
                    imageProxy.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    onFailure(exception)
                }
            }
        )
    }

    @SuppressLint("RestrictedApi", "MissingPermission")
    fun startRecording(
        onSuccess: (videoFile: File) -> Unit,
        onFailure: (videoCaptureError: Int, message: String, cause: Throwable?) -> Unit,
    ) {
        val videoFile = fileService.getCacheFile("current-recording.mp4")
        val outputOptions = VideoCapture.OutputFileOptions.Builder(videoFile).build()
        videoCapture.startRecording(outputOptions, ContextCompat.getMainExecutor(context),
            object : VideoCapture.OnVideoSavedCallback {
                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    onFailure(videoCaptureError, message, cause)
                }

                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    onSuccess(videoFile)
                }
            })
    }

    @SuppressLint("RestrictedApi")
    fun stopRecording() {
        videoCapture.stopRecording()
    }
}