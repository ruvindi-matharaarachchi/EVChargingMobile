package com.example.evchargingmobile.ui.owner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.ReservationDao
import com.example.evchargingmobile.ui.auth.LoginActivity
import com.example.evchargingmobile.ui.station.StationListActivity
import com.example.evchargingmobile.utils.DateTime

class OwnerDashboardActivity : AppCompatActivity() {
    private lateinit var reservationDao: ReservationDao
    private var ownerNic: String? = null
    private var ownerName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_dashboard)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        reservationDao = ReservationDao(db)

        // Get owner info from intent
        ownerNic = intent.getStringExtra("ownerNic")
        ownerName = intent.getStringExtra("ownerName")

        if (ownerNic == null) {
            // Redirect to login if no owner info
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupUI()
        loadStats()
        setupClickListeners()
    }

    private fun setupUI() {
        findViewById<android.widget.TextView>(R.id.tvOwnerName).text = ownerName ?: "User"
    }

    private fun loadStats() {
        val reservations = reservationDao.listByOwner(ownerNic!!)
        val now = DateTime.now()

        val activeBookings = reservations.count { 
            it.status in listOf("PENDING", "APPROVED") && it.slotTime > now 
        }
        val totalSessions = reservations.count { 
            it.status == "COMPLETED" || (it.status == "APPROVED" && it.slotTime <= now)
        }

        findViewById<android.widget.TextView>(R.id.tvActiveBookings).text = activeBookings.toString()
        findViewById<android.widget.TextView>(R.id.tvTotalSessions).text = totalSessions.toString()
    }

    private fun setupClickListeners() {
        findViewById<android.widget.Button>(R.id.btnViewStations).setOnClickListener {
            startActivity(Intent(this, StationListActivity::class.java))
        }

        findViewById<android.widget.Button>(R.id.btnBookSlot).setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("ownerNic", ownerNic)
            startActivity(intent)
        }

        findViewById<android.widget.Button>(R.id.btnMyBookings).setOnClickListener {
            val intent = Intent(this, MyReservationsActivity::class.java)
            intent.putExtra("ownerNic", ownerNic)
            startActivity(intent)
        }

        findViewById<android.widget.Button>(R.id.btnProfile).setOnClickListener {
            val intent = Intent(this, OwnerEditActivity::class.java)
            intent.putExtra("nic", ownerNic)
            startActivity(intent)
        }

        findViewById<android.widget.Button>(R.id.btnLogout).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }
}



