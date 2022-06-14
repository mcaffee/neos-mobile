package com.ictis.neos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ictis.neos.auth.AuthService

//import com.ictis.pdmp.core.USER_LOGGED_IN
//import com.ictis.pdmp.core.USER_NAME
import com.ictis.neos.ui.settings.SettingsFragment
import com.ictis.neos.ui.recognition.RecognitionFragment
import com.ictis.neos.ui.training.TrainingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

class MainActivity : AppCompatActivity(), KoinComponent {
    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    private val authService: AuthService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLogin()

        if (hasNoPermissions()) {
            requestPermissions()
        }


        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // drawer menu
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val mOnNavigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_recognition -> {
                    val fragment = RecognitionFragment()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment, fragment.javaClass.simpleName)
                            .addToBackStack(null)
                            .commit()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_training -> {
                    val fragment = TrainingFragment()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment, fragment.javaClass.simpleName)
                            .addToBackStack(null)
                            .commit()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_settings -> {
                    val fragment = SettingsFragment()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment, fragment.javaClass.simpleName)
                            .addToBackStack(null)
                            .commit()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@OnNavigationItemSelectedListener true
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            false
        }
        navView.setNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (savedInstanceState == null) {
            navView.menu.performIdentifierAction(R.id.nav_recognition, 0)
        }

        // set nav header username
        val headerLayout = navView.getHeaderView(0)
        val textViewUsername: TextView = headerLayout.findViewById(R.id.textview_username)
        val userLogin = intent.getStringExtra("com.ictis.pdmp.Username")
        textViewUsername.text = userLogin ?: "Default"
        Log.d(TAG, "onCreate: textViewUserName=$textViewUsername")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onBackPressed() {
        val backCount: Int = supportFragmentManager.backStackEntryCount
        if (backCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    private fun checkLogin() {
        Log.d(TAG, "checkLogin: checking")
        val intent = Intent(this, LoginActivity::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            if (!authService.checkAuth()) {
                startActivity(intent)
            } else {
                Log.d(TAG, "checkLogin: API key present and valid")
            }
        }
    }

    private fun isTesting(): Boolean {
        return try {
            Class.forName("com.ictis.pdmp.acceptance.LoginActivityAcceptanceTest")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    private fun hasNoPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this, requiredPermissions,0)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
