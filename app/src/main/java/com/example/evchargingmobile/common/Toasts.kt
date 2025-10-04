package com.example.evchargingmobile.common

import android.content.Context
import android.widget.Toast

object Toasts {
    
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
    
    fun showError(context: Context, message: String) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
    }
    
    fun showSuccess(context: Context, message: String) {
        Toast.makeText(context, "Success: $message", Toast.LENGTH_SHORT).show()
    }
}
