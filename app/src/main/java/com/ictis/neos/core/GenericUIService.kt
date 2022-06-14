package com.ictis.neos.core

import android.content.Context
import android.os.Looper
import android.widget.Toast

class GenericUIService {
    fun showToast(context: Context, resId: Int, length: Int = Toast.LENGTH_LONG) {
        checkLooper()
        Toast.makeText(context, context.getString(resId), length).show()
    }

    fun checkLooper() {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
    }
}