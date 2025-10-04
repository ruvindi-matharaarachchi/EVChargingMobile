package com.example.evchargingmobile.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evchargingmobile.R
import com.example.evchargingmobile.auth.AuthActivity
import com.example.evchargingmobile.auth.SessionStore
import com.example.evchargingmobile.common.Toasts
import com.example.evchargingmobile.data.dto.OperatorDto
import com.example.evchargingmobile.data.repo.AdminRepository
import kotlinx.coroutines.launch

class ManageOperatorsActivity : AppCompatActivity() {
    
    private lateinit var sessionStore: SessionStore
    private lateinit var adminRepository: AdminRepository
    private lateinit var operatorsAdapter: OperatorsAdapter
    private val operators = mutableListOf<OperatorDto>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_operators)
        
        sessionStore = SessionStore(this)
        adminRepository = AdminRepository(com.example.evchargingmobile.data.api.AdminApi())
        
        // Check authentication
        if (!sessionStore.isLoggedIn()) {
            navigateToAuth()
            return
        }
        
        setupUI()
        loadOperators()
    }
    
    private fun setupUI() {
        // Setup RecyclerView
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewOperators)
        operatorsAdapter = OperatorsAdapter(operators) { operator, action ->
            when (action) {
                "edit" -> {
                    val intent = Intent(this, OperatorEditorActivity::class.java)
                    intent.putExtra("operatorId", operator.id)
                    intent.putExtra("isEdit", true)
                    startActivity(intent)
                }
                "deactivate" -> {
                    deactivateOperator(operator.id)
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = operatorsAdapter
        
        // FAB for adding new operator
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddOperator).setOnClickListener {
            val intent = Intent(this, OperatorEditorActivity::class.java)
            intent.putExtra("isEdit", false)
            startActivity(intent)
        }
        
        // Back button
        findViewById<android.widget.Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
    
    private fun loadOperators() {
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                when (val result = adminRepository.listOperators(token = token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        operators.clear()
                        operators.addAll(result.data)
                        operatorsAdapter.notifyDataSetChanged()
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@ManageOperatorsActivity, "Failed to load operators: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@ManageOperatorsActivity, "Failed to load operators: ${e.message}")
            }
        }
    }
    
    private fun deactivateOperator(id: String) {
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                when (val result = adminRepository.deactivateOperator(id, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        Toasts.showSuccess(this@ManageOperatorsActivity, "Operator deactivated successfully")
                        loadOperators() // Refresh list
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@ManageOperatorsActivity, "Failed to deactivate operator: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@ManageOperatorsActivity, "Failed to deactivate operator: ${e.message}")
            }
        }
    }
    
    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onResume() {
        super.onResume()
        loadOperators() // Refresh when returning from editor
    }
}
