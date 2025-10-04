package com.example.evchargingmobile.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.dto.OwnerDto

class OwnersAdapter(
    private val owners: List<OwnerDto>,
    private val onActionClick: (OwnerDto, String) -> Unit
) : RecyclerView.Adapter<OwnersAdapter.OwnerViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_owner, parent, false)
        return OwnerViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
        val owner = owners[position]
        holder.bind(owner)
    }
    
    override fun getItemCount(): Int = owners.size
    
    inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNic: TextView = itemView.findViewById(R.id.tvNic)
        private val tvFullName: TextView = itemView.findViewById(R.id.tvFullName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnEdit: android.widget.Button = itemView.findViewById(R.id.btnEdit)
        private val btnDeactivate: android.widget.Button = itemView.findViewById(R.id.btnDeactivate)
        
        fun bind(owner: OwnerDto) {
            tvNic.text = owner.nic
            tvFullName.text = owner.fullName
            tvEmail.text = owner.email ?: "No email"
            tvPhone.text = owner.phone ?: "No phone"
            tvStatus.text = owner.status
            
            // Set status color
            val statusColor = if (owner.status == "ACTIVE") {
                android.graphics.Color.GREEN
            } else {
                android.graphics.Color.RED
            }
            tvStatus.setTextColor(statusColor)
            
            btnEdit.setOnClickListener {
                onActionClick(owner, "edit")
            }
            
            btnDeactivate.setOnClickListener {
                onActionClick(owner, "deactivate")
            }
            
            // Disable deactivate button if already deactivated
            btnDeactivate.isEnabled = owner.status == "ACTIVE"
        }
    }
}
