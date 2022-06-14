package com.ictis.neos.common


object CommonConstants {
    val signChars = listOf(
        "А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч",
        "Ш", "Ъ", "Ы", "Ь", "Э", "Ю", "Я"
    )
    val signCharViewModelList = ArrayList<ScrollPickerViewModel>()

    init {
        for(i in signChars) {
            val vm = ScrollPickerViewModel(i.toString())
            signCharViewModelList.add(vm)
        }
    }
}