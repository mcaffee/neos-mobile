package com.ictis.neos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import com.ictis.neos.auth.AuthService
import com.ictis.neos.core.GenericUIService

class LoginActivity : AppCompatActivity() {
    private val authService: AuthService by inject()
    private val uiService: GenericUIService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginUser(v: View) {
        val apiKey: EditText = v.rootView.findViewById(R.id.edittext_api_key)
        authService.storeKey(apiKey.text.toString())

        GlobalScope.launch(Dispatchers.IO) {
            if (!authService.pingApi()) {
                uiService.showToast(applicationContext, R.string.login_error_message_wrong_api_key)
            } else {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
