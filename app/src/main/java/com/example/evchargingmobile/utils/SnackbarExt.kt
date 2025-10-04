package com.example.evchargingmobile.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snack(msg: String) {
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()
}

