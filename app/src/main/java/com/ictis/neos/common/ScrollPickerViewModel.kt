package com.ictis.neos.common

import androidx.databinding.ObservableField

data class ScrollPickerViewModel(val index: String) {
    val obsIndex = ObservableField<String>()

    init {
        obsIndex.set(index)
    }
}