package com.example.evchargingmobile.ui.owner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.ReservationDao
import com.example.evchargingmobile.domain.Reservation
import com.example.evchargingmobile.utils.DateTime
import com.example.evchargingmobile.utils.Validators
import com.example.evchargingmobile.utils.snack
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyReservationsActivity : AppCompatActivity() {
    private lateinit var reservationDao: ReservationDao
    private lateinit var adapter: ReservationAdapter
    private var ownerNic: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_reservations)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        reservationDao = ReservationDao(db)

        // Get owner NIC from intent
        ownerNic = intent.getStringExtra("ownerNic")
        if (ownerNic == null) {
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("No owner selected")
            finish()
            return
        }

        setupRecyclerView()
        loadReservations()

        findViewById<FloatingActionButton>(R.id.fabBook).setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("ownerNic", ownerNic)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvReservations)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReservationAdapter { reservation, action ->
            when (action) {
                "reschedule" -> rescheduleReservation(reservation)
                "cancel" -> cancelReservation(reservation)
            }
        }
        recyclerView.adapter = adapter
    }

    private fun loadReservations() {
        val reservations = reservationDao.listByOwner(ownerNic!!)
        adapter.updateReservations(reservations)
    }

    private fun rescheduleReservation(reservation: Reservation) {
        val now = DateTime.now()
        if (!Validators.canModifyOrCancel(now, reservation.slotTime)) {
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("Cannot reschedule within 12 hours")
            return
        }

        val intent = Intent(this, RescheduleActivity::class.java)
        intent.putExtra("reservationId", reservation.id)
        startActivity(intent)
    }

    private fun cancelReservation(reservation: Reservation) {
        val now = DateTime.now()
        if (!Validators.canModifyOrCancel(now, reservation.slotTime)) {
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("Cannot cancel within 12 hours")
            return
        }

        // Show cancel dialog
        val dialog = CancelDialog()
        dialog.setReservation(reservation) {
            reservationDao.updateStatus(reservation.id, "CANCELLED")
            loadReservations()
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("Reservation cancelled")
        }
        dialog.show(supportFragmentManager, "cancel_dialog")
    }

    override fun onResume() {
        super.onResume()
        loadReservations()
    }
}

class ReservationAdapter(
    private val onActionClick: (Reservation, String) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {
    private var reservations = listOf<Reservation>()

    fun updateReservations(newReservations: List<Reservation>) {
        reservations = newReservations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reservations[position], onActionClick)
    }

    override fun getItemCount() = reservations.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        fun bind(reservation: Reservation, onActionClick: (Reservation, String) -> Unit) {
            val now = DateTime.now()
            val canModify = Validators.canModifyOrCancel(now, reservation.slotTime)

            itemView.findViewById<android.widget.TextView>(R.id.tvStationId).text = reservation.stationId
            itemView.findViewById<android.widget.TextView>(R.id.tvDateTime).text = DateTime.formatShort(reservation.slotTime)
            
            val statusChip = itemView.findViewById<com.google.android.material.chip.Chip>(R.id.chipStatus)
            statusChip.text = reservation.status

            val btnReschedule = itemView.findViewById<android.widget.Button>(R.id.btnReschedule)
            val btnCancel = itemView.findViewById<android.widget.Button>(R.id.btnCancel)

            btnReschedule.isEnabled = canModify && reservation.status == "PENDING"
            btnCancel.isEnabled = canModify && reservation.status in listOf("PENDING", "APPROVED")

            btnReschedule.setOnClickListener { onActionClick(reservation, "reschedule") }
            btnCancel.setOnClickListener { onActionClick(reservation, "cancel") }
        }
    }
}
