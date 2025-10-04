package com.example.evchargingmobile.ui.common

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.ReservationDao
import com.example.evchargingmobile.utils.DateTime
import com.example.evchargingmobile.utils.snack

class DashboardActivity : AppCompatActivity() {
    private lateinit var reservationDao: ReservationDao
    private var ownerNic: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        reservationDao = ReservationDao(db)

        // Get owner NIC from intent
        ownerNic = intent.getStringExtra("ownerNic")
        if (ownerNic == null) {
            findViewById<android.widget.LinearLayout>(R.id.root).snack("No owner selected")
            finish()
            return
        }

        loadDashboardData()
    }

    private fun loadDashboardData() {
        val reservations = reservationDao.listByOwner(ownerNic!!)
        val now = DateTime.now()

        val pendingCount = reservations.count { it.status == "PENDING" }
        val approvedCount = reservations.count { 
            it.status == "APPROVED" && it.slotTime > now 
        }
        val cancelledCount = reservations.count { it.status == "CANCELLED" }
        val completedCount = reservations.count { 
            it.status == "COMPLETED" || (it.status == "APPROVED" && it.slotTime <= now)
        }

        findViewById<android.widget.TextView>(R.id.tvPendingCount).text = pendingCount.toString()
        findViewById<android.widget.TextView>(R.id.tvApprovedCount).text = approvedCount.toString()
        findViewById<android.widget.TextView>(R.id.tvCancelledCount).text = cancelledCount.toString()
        findViewById<android.widget.TextView>(R.id.tvCompletedCount).text = completedCount.toString()
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }
}

