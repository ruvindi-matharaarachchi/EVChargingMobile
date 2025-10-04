package com.example.evchargingmobile.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.evchargingmobile.R
import com.example.evchargingmobile.data.dto.OperatorDto

class OperatorsAdapter(
    private val operators: List<OperatorDto>,
    private val onActionClick: (OperatorDto, String) -> Unit
) : RecyclerView.Adapter<OperatorsAdapter.OperatorViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperatorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_operator, parent, false)
        return OperatorViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: OperatorViewHolder, position: Int) {
        val operator = operators[position]
        holder.bind(operator)
    }
    
    override fun getItemCount(): Int = operators.size
    
    inner class OperatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvFullName: TextView = itemView.findViewById(R.id.tvFullName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnEdit: android.widget.Button = itemView.findViewById(R.id.btnEdit)
        private val btnDeactivate: android.widget.Button = itemView.findViewById(R.id.btnDeactivate)
        
        fun bind(operator: OperatorDto) {
            tvId.text = operator.id
            tvFullName.text = operator.fullName
            tvEmail.text = operator.email ?: "No email"
            tvPhone.text = operator.phone ?: "No phone"
            tvStatus.text = operator.status
            
            // Set status color
            val statusColor = if (operator.status == "ACTIVE") {
                android.graphics.Color.GREEN
            } else {
                android.graphics.Color.RED
            }
            tvStatus.setTextColor(statusColor)
            
            btnEdit.setOnClickListener {
                onActionClick(operator, "edit")
            }
            
            btnDeactivate.setOnClickListener {
                onActionClick(operator, "deactivate")
            }
            
            // Disable deactivate button if already deactivated
            btnDeactivate.isEnabled = operator.status == "ACTIVE"
        }
    }
}
