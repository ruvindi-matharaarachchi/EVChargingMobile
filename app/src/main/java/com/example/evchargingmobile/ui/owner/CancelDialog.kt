package com.example.evchargingmobile.ui.owner

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.evchargingmobile.R
import com.example.evchargingmobile.domain.Reservation

class CancelDialog : DialogFragment() {
    private var reservation: Reservation? = null
    private var onConfirm: (() -> Unit)? = null

    fun setReservation(reservation: Reservation, onConfirm: () -> Unit) {
        this.reservation = reservation
        this.onConfirm = onConfirm
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_cancel)
            .create()
    }

    override fun onStart() {
        super.onStart()
        
        dialog?.findViewById<android.widget.Button>(R.id.btnCancel)?.setOnClickListener {
            dismiss()
        }
        
        dialog?.findViewById<android.widget.Button>(R.id.btnConfirmCancel)?.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
    }
}

