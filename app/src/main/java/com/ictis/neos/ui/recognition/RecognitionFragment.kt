package com.ictis.neos.ui.recognition

import android.media.Image
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import com.ictis.neos.R
import com.ictis.neos.camera.CameraService
import com.ictis.neos.core.GenericUIService
import com.ictis.neos.core.ImageUtils
import com.ictis.neos.core.SharedPreferencesService
import com.ictis.neos.recognition.RecognitionService

class RecognitionFragment : Fragment(), KoinComponent {
    private val uiService: GenericUIService by inject()
    private val cameraService: CameraService by inject()
    private val recognitionService: RecognitionService by inject()
    private val sharedPreferencesService: SharedPreferencesService by inject()

    lateinit var editText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate fragment layout
        val root = inflater.inflate(R.layout.fragment_recognition, container, false)

        // Init camera
        cameraService.init(requireContext(), this as LifecycleOwner, this::onCameraBound)

        // FABs
        val fabCamera: FloatingActionButton = root.findViewById(R.id.fab_camera)
        fabCamera.setOnClickListener { takePictureAndGetSuggestions() }

        // Text view
        editText = root.findViewById(R.id.edittext)

        val modelPath = sharedPreferencesService.getString(R.string.preference_usr_model_path)
        recognitionService.loadModel(requireActivity(), modelPath)

        return root
    }

    private fun onCameraBound() {
        val previewView: PreviewView = requireView().findViewById(R.id.preview_view)
        cameraService.preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    private fun takePictureAndGetSuggestions() {
        Log.d(TAG, "takePictureAndGetSuggestions: taking snapshot")
        cameraService.takePicture(
            { imageProxy ->
                @androidx.camera.core.ExperimentalGetImage
                val image: Image = imageProxy.image!!

                var bitmap = ImageUtils.imageToBitmap(image)
                // rotate to 90 degrees in debug mode. why??
                if (requireContext().getString(R.string.build_type) == "DEBUG") {
                    bitmap = ImageUtils.rotate(bitmap, 90F)
                }

                // send training data
                GlobalScope.launch(Dispatchers.IO) {
                    val suggestions = recognitionService.getSuggestions(bitmap)
                    Log.d(TAG, "takePictureAndGetSuggestions: suggestions received: ${suggestions.contentToString()}")

                    // only if suggestions found
                    uiService.checkLooper()
                    if (suggestions.count() > 0) {
                        editText.text.append(suggestions[0])
                    }
                }
            },
            { e->
                Log.e(TAG,"takePictureAndGetSuggestions: cannot take image", e)
            }
        )
    }

    companion object {
        private const val TAG = "TrainingFragment"
    }
}
