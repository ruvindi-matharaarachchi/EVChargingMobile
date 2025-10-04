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
import com.example.evchargingmobile.data.dto.OwnerDto
import com.example.evchargingmobile.data.repo.AdminRepository
import kotlinx.coroutines.launch

class ManageOwnersActivity : AppCompatActivity() {
    
    private lateinit var sessionStore: SessionStore
    private lateinit var adminRepository: AdminRepository
    private lateinit var ownersAdapter: OwnersAdapter
    private val owners = mutableListOf<OwnerDto>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_owners)
        
        sessionStore = SessionStore(this)
        adminRepository = AdminRepository(com.example.evchargingmobile.data.api.AdminApi())
        
        // Check authentication
        if (!sessionStore.isLoggedIn()) {
            navigateToAuth()
            return
        }
        
        setupUI()
        loadOwners()
    }
    
    private fun setupUI() {
        // Setup RecyclerView
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewOwners)
        ownersAdapter = OwnersAdapter(owners) { owner, action ->
            when (action) {
                "edit" -> {
                    val intent = Intent(this, OwnerEditorActivity::class.java)
                    intent.putExtra("ownerNic", owner.nic)
                    intent.putExtra("isEdit", true)
                    startActivity(intent)
                }
                "deactivate" -> {
                    deactivateOwner(owner.nic)
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ownersAdapter
        
        // FAB for adding new owner
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddOwner).setOnClickListener {
            val intent = Intent(this, OwnerEditorActivity::class.java)
            intent.putExtra("isEdit", false)
            startActivity(intent)
        }
        
        // Back button
        findViewById<android.widget.Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
    
    private fun loadOwners() {
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                when (val result = adminRepository.listOwners(token = token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        owners.clear()
                        owners.addAll(result.data)
                        ownersAdapter.notifyDataSetChanged()
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@ManageOwnersActivity, "Failed to load owners: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@ManageOwnersActivity, "Failed to load owners: ${e.message}")
            }
        }
    }
    
    private fun deactivateOwner(nic: String) {
        val token = sessionStore.getToken() ?: return
        
        lifecycleScope.launch {
            try {
                when (val result = adminRepository.deactivateOwner(nic, token)) {
                    is com.example.evchargingmobile.utils.Result.Success -> {
                        Toasts.showSuccess(this@ManageOwnersActivity, "Owner deactivated successfully")
                        loadOwners() // Refresh list
                    }
                    is com.example.evchargingmobile.utils.Result.Error -> {
                        Toasts.showError(this@ManageOwnersActivity, "Failed to deactivate owner: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Toasts.showError(this@ManageOwnersActivity, "Failed to deactivate owner: ${e.message}")
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
        loadOwners() // Refresh when returning from editor
    }
}
