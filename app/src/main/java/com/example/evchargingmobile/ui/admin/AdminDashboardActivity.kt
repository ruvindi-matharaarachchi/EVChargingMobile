package com.example.evchargingmobile.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.OwnerDao
import com.example.evchargingmobile.data.local.ReservationDao
import com.example.evchargingmobile.data.local.StationDao
import com.example.evchargingmobile.ui.auth.LoginActivity
import com.example.evchargingmobile.utils.snack

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var ownerDao: OwnerDao
    private lateinit var reservationDao: ReservationDao
    private lateinit var stationDao: StationDao
    private var adminNic: String? = null
    private var adminName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        ownerDao = OwnerDao(db)
        reservationDao = ReservationDao(db)
        stationDao = StationDao(db)

        // Get admin info from intent
        adminNic = intent.getStringExtra("adminNic")
        adminName = intent.getStringExtra("adminName")

        if (adminNic == null) {
            // Redirect to login if no admin info
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupUI()
        loadStats()
        setupClickListeners()
    }

    private fun setupUI() {
        findViewById<android.widget.TextView>(R.id.tvAdminName).text = adminName ?: "Administrator"
    }

    private fun loadStats() {
        // Load total users
        val allUsers = ownerDao.getAllUsers()
        val totalUsers = allUsers.size
        findViewById<android.widget.TextView>(R.id.tvTotalUsers).text = totalUsers.toString()

        // Load active stations
        val allStations = stationDao.listAll()
        val activeStations = allStations.count { it.active }
        findViewById<android.widget.TextView>(R.id.tvActiveStations).text = activeStations.toString()

        // Load total bookings
        val allReservations = reservationDao.getAllReservations()
        val totalBookings = allReservations.size
        findViewById<android.widget.TextView>(R.id.tvTotalBookings).text = totalBookings.toString()

        // Calculate revenue (mock calculation)
        val completedBookings = allReservations.count { it.status == "COMPLETED" }
        val revenue = completedBookings * 500 // Rs. 500 per session
        findViewById<android.widget.TextView>(R.id.tvRevenue).text = "Rs. $revenue"
    }

    private fun setupClickListeners() {
        findViewById<android.widget.Button>(R.id.btnManageUsers).setOnClickListener {
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("User Management - Coming Soon")
        }

        findViewById<android.widget.Button>(R.id.btnManageStations).setOnClickListener {
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("Station Management - Coming Soon")
        }

        findViewById<android.widget.Button>(R.id.btnViewBookings).setOnClickListener {
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("All Bookings - Coming Soon")
        }

        findViewById<android.widget.Button>(R.id.btnSystemSettings).setOnClickListener {
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("System Settings - Coming Soon")
        }

        findViewById<android.widget.Button>(R.id.btnReports).setOnClickListener {
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("Reports & Analytics - Coming Soon")
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


