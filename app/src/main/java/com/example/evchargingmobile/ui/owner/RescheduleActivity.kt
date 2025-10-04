package com.example.evchargingmobile.ui.owner

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.ReservationDao
import com.example.evchargingmobile.data.remote.ReservationApi
import com.example.evchargingmobile.domain.Reservation
import com.example.evchargingmobile.utils.DateTime
import com.example.evchargingmobile.utils.Validators
import com.example.evchargingmobile.utils.snack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RescheduleActivity : AppCompatActivity() {
    private lateinit var reservationDao: ReservationDao
    private var reservation: Reservation? = null
    private var newDateTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reschedule)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        reservationDao = ReservationDao(db)

        val reservationId = intent.getStringExtra("reservationId")
        if (reservationId == null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("No reservation selected")
            finish()
            return
        }

        loadReservation(reservationId)
        setupDateTimePickers()
        setupConfirmButton()
    }

    private fun loadReservation(reservationId: String) {
        // Find reservation by ID (simplified - in real app, add getById method to DAO)
        val allReservations = reservationDao.listByOwner("") // This is a simplified approach
        reservation = allReservations.find { it.id == reservationId }
        
        if (reservation == null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Reservation not found")
            finish()
            return
        }

        findViewById<android.widget.TextView>(R.id.tvCurrentSlot).text = DateTime.formatShort(reservation!!.slotTime)
    }

    private fun setupDateTimePickers() {
        findViewById<android.widget.Button>(R.id.btnNewDate).setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    updateNewDateTime(selectedDate.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        findViewById<android.widget.Button>(R.id.btnNewTime).setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }
                    updateNewDateTime(selectedTime.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun updateNewDateTime(millis: Long) {
        newDateTime = millis
        findViewById<android.widget.TextView>(R.id.tvNewDateTime).text = DateTime.formatShort(millis)
    }

    private fun setupConfirmButton() {
        findViewById<android.widget.Button>(R.id.btnConfirmReschedule).setOnClickListener {
            confirmReschedule()
        }
    }

    private fun confirmReschedule() {
        val newSlotTime = newDateTime
        if (newSlotTime == null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Please select new date and time")
            return
        }

        val now = DateTime.now()
        if (!Validators.canModifyOrCancel(now, newSlotTime)) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Cannot reschedule within 12 hours")
            return
        }

        if (!Validators.isWithin7Days(now, newSlotTime)) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("New slot must be within 7 days")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = ReservationApi.reschedule(reservation!!.id, newSlotTime)
                if (result is com.example.evchargingmobile.utils.Result.Success) {
                    // Update local database
                    reservationDao.updateTime(reservation!!.id, newSlotTime)
                    
                    runOnUiThread {
                        findViewById<android.widget.ScrollView>(R.id.root).snack("Reschedule confirmed!")
                        setResult(RESULT_OK)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        findViewById<android.widget.ScrollView>(R.id.root).snack("Failed to reschedule")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    findViewById<android.widget.ScrollView>(R.id.root).snack("Error: ${e.message}")
                }
            }
        }
    }
}
