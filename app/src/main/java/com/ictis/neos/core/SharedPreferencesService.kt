package com.ictis.neos.core

import android.content.Context
import android.content.SharedPreferences
import com.ictis.neos.MainApplication
import com.ictis.neos.R

class SharedPreferencesService {

    fun getString(resId: Int): String? {
        val context = MainApplication.instance.applicationContext
        val sharedPref = getSharedPreferences()

        return sharedPref.getString(context.getString(resId), null)
    }

    fun putString(resId: Int, value: String?) {
        val context = MainApplication.instance.applicationContext
        val sharedPref = getSharedPreferences()
        with(sharedPref.edit()) {
            putString(context.getString(resId), value)
            apply()
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        val context = MainApplication.instance.applicationContext
        return context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
    }
}