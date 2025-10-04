package com.example.evchargingmobile.ui.station

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.local.AppDbHelper
import com.example.evchargingmobile.data.local.StationDao
import com.example.evchargingmobile.domain.Station
import com.example.evchargingmobile.ui.owner.BookingActivity
import com.example.evchargingmobile.utils.snack
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StationListActivity : AppCompatActivity() {
    private lateinit var stationDao: StationDao
    private lateinit var adapter: StationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_station_list)

        val dbHelper = AppDbHelper(this)
        val db = dbHelper.writableDatabase
        stationDao = StationDao(db)

        setupRecyclerView()
        loadStations()
        setupRefreshButton()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvStations)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StationAdapter { station ->
            // Navigate to booking with selected station
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("stationId", station.stationId)
            intent.putExtra("stationName", station.name)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun loadStations() {
        // For demo purposes, create some sample stations
        val sampleStations = listOf(
            Station("ST001", "Colombo Central Station", "AC", 6.9271, 79.8612, 5, true),
            Station("ST002", "Kandy City Station", "DC", 7.2906, 80.6337, 3, true),
            Station("ST003", "Galle Fort Station", "AC", 6.0535, 80.2210, 4, true),
            Station("ST004", "Negombo Airport Station", "DC", 7.2086, 79.8358, 6, true),
            Station("ST005", "Anuradhapura Station", "AC", 8.3114, 80.4037, 2, false)
        )

        // Save sample stations to database
        stationDao.bulkUpsert(sampleStations)

        // Load from database
        val stations = stationDao.listAll()
        adapter.updateStations(stations)
    }

    private fun setupRefreshButton() {
        findViewById<FloatingActionButton>(R.id.fabRefresh).setOnClickListener {
            loadStations()
            findViewById<androidx.coordinatorlayout.widget.CoordinatorLayout>(R.id.root).snack("Stations refreshed")
        }
    }
}

class StationAdapter(
    private val onStationClick: (Station) -> Unit
) : RecyclerView.Adapter<StationAdapter.ViewHolder>() {
    private var stations = listOf<Station>()

    fun updateStations(newStations: List<Station>) {
        stations = newStations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_station, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stations[position], onStationClick)
    }

    override fun getItemCount() = stations.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        fun bind(station: Station, onStationClick: (Station) -> Unit) {
            itemView.findViewById<android.widget.TextView>(R.id.tvStationId).text = station.stationId
            itemView.findViewById<android.widget.TextView>(R.id.tvType).text = station.type
            itemView.findViewById<android.widget.TextView>(R.id.tvAvailableSlots).text = station.available.toString()
            itemView.findViewById<android.widget.TextView>(R.id.tvLocation).text = "Location ${station.lat.toInt()}, ${station.lng.toInt()}"

            val statusChip = itemView.findViewById<com.google.android.material.chip.Chip>(R.id.chipStatus)
            val btnBook = itemView.findViewById<android.widget.Button>(R.id.btnBookNow)

            if (station.active && station.available > 0) {
                statusChip.text = "Available"
                statusChip.setChipBackgroundColorResource(android.R.color.holo_green_light)
                btnBook.isEnabled = true
                btnBook.text = "Book Now"
            } else if (station.active && station.available == 0) {
                statusChip.text = "Full"
                statusChip.setChipBackgroundColorResource(android.R.color.holo_orange_light)
                btnBook.isEnabled = false
                btnBook.text = "Fully Booked"
            } else {
                statusChip.text = "Offline"
                statusChip.setChipBackgroundColorResource(android.R.color.darker_gray)
                btnBook.isEnabled = false
                btnBook.text = "Station Offline"
            }

            btnBook.setOnClickListener {
                if (station.active && station.available > 0) {
                    onStationClick(station)
                }
            }
        }
    }
}



