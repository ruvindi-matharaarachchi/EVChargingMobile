package com.example.evchargingmobile.ui.owner

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class BookingActivity : AppCompatActivity() {
    private lateinit var reservationDao: ReservationDao
    private var ownerNic: String? = null
    private var stationId: String? = null
    private var stationName: String? = null
    private var selectedTimeSlot: Long? = null
    private lateinit var timeSlotAdapter: TimeSlotAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        reservationDao = ReservationDao(db)

        ownerNic = intent.getStringExtra("ownerNic")
        stationId = intent.getStringExtra("stationId")
        stationName = intent.getStringExtra("stationName")

        if (ownerNic == null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("No owner selected")
            finish()
            return
        }

        setupUI()
        setupTimeSlots()
        setupConfirmButton()
    }

    private fun setupUI() {
        if (stationName != null) {
            findViewById<android.widget.TextView>(R.id.tvSelectedStation).text = stationName
        } else {
            findViewById<android.widget.TextView>(R.id.tvSelectedStation).text = "Please select a station"
        }
    }

    private fun setupTimeSlots() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvTimeSlots)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        
        timeSlotAdapter = TimeSlotAdapter { timeSlot ->
            selectedTimeSlot = timeSlot
            findViewById<android.widget.TextView>(R.id.tvSelectedDateTime).text = DateTime.formatShort(timeSlot)
        }
        recyclerView.adapter = timeSlotAdapter

        // Generate time slots for today and next 6 days
        generateTimeSlots()
    }

    private fun generateTimeSlots() {
        val timeSlots = mutableListOf<Long>()
        val calendar = Calendar.getInstance()
        
        // Generate slots for next 7 days
        for (day in 0..6) {
            val dayCalendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 8) // Start at 8 AM
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            // Generate hourly slots from 8 AM to 8 PM
            for (hour in 8..20) {
                dayCalendar.set(Calendar.HOUR_OF_DAY, hour)
                timeSlots.add(dayCalendar.timeInMillis)
            }
        }
        
        timeSlotAdapter.updateTimeSlots(timeSlots)
    }

    private fun setupConfirmButton() {
        findViewById<android.widget.Button>(R.id.btnConfirm).setOnClickListener {
            confirmBooking()
        }
    }

    private fun confirmBooking() {
        val slotTime = selectedTimeSlot

        if (stationId == null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Please select a station first")
            return
        }

        if (slotTime == null) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Please select a time slot")
            return
        }

        val now = DateTime.now()
        if (!Validators.isWithin7Days(now, slotTime)) {
            findViewById<android.widget.ScrollView>(R.id.root).snack("Booking must be within 7 days")
            return
        }

        // Create reservation
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = ReservationApi.create(ownerNic!!, stationId!!, slotTime)
                if (result is com.example.evchargingmobile.utils.Result.Success) {
                    // Save to local database
                    reservationDao.upsert(result.data)
                    
                    runOnUiThread {
                        findViewById<android.widget.ScrollView>(R.id.root).snack("Booking confirmed!")
                        setResult(RESULT_OK)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        findViewById<android.widget.ScrollView>(R.id.root).snack("Failed to create booking")
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

class TimeSlotAdapter(
    private val onTimeSlotClick: (Long) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {
    private var timeSlots = listOf<Long>()

    fun updateTimeSlots(newTimeSlots: List<Long>) {
        timeSlots = newTimeSlots
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_slot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(timeSlots[position], onTimeSlotClick)
    }

    override fun getItemCount() = timeSlots.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        fun bind(timeSlot: Long, onTimeSlotClick: (Long) -> Unit) {
            val calendar = Calendar.getInstance().apply { timeInMillis = timeSlot }
            val timeString = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
            
            itemView.findViewById<android.widget.Button>(R.id.btnTimeSlot).text = timeString
            itemView.findViewById<android.widget.Button>(R.id.btnTimeSlot).setOnClickListener {
                onTimeSlotClick(timeSlot)
            }
        }
    }
}