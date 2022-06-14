package com.ictis.neos.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import com.ictis.neos.R
import com.ictis.neos.core.GenericUIService
import com.ictis.neos.training.TrainingService

class SettingsFragment : Fragment(), KoinComponent {
    private val trainingService: TrainingService by inject()
    private val uiService: GenericUIService by inject()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val buttonTrain: Button = root.findViewById(R.id.button_train)
        buttonTrain.setOnClickListener { trainUserModel() }

        val buttonDownload: Button = root.findViewById(R.id.button_download)
        buttonDownload.setOnClickListener { downloadUserModel() }

        val buttonDelete: Button = root.findViewById(R.id.button_delete)
        buttonDelete.setOnClickListener { deleteUserModel() }

        return root
    }

    private fun trainUserModel() {
        Log.d(TAG, "trainClassifier: running")
        GlobalScope.launch(Dispatchers.IO) {
            trainingService.trainUserModel()
        }
    }

    private fun downloadUserModel() {
        Log.d(TAG, "downloadModel: running")
        GlobalScope.launch(Dispatchers.IO) {
            trainingService.downloadUserModel()
            uiService.showToast(requireContext(), R.string.training_user_model_download_finished_text)
        }
    }

    private fun deleteUserModel() {
        Log.d(TAG, "deleteUserModel: running")
        GlobalScope.launch(Dispatchers.IO) {
            trainingService.deleteUserModel()
            uiService.showToast(requireContext(), R.string.training_user_model_deleted_text)
        }
    }

    companion object {
        private const val TAG = "SettingsFragment"
    }

}
