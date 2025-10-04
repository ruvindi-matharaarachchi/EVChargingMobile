package com.example.evchargingmobile.auth

object AuthValidator {
    
    fun validateNic(nic: String): String? {
        if (nic.isEmpty()) {
            return "NIC is required"
        }
        if (nic.length < 10) {
            return "NIC must be at least 10 characters"
        }
        return null
    }
    
    fun validateFullName(fullName: String): String? {
        if (fullName.isEmpty()) {
            return "Full name is required"
        }
        if (fullName.length < 3) {
            return "Full name must be at least 3 characters"
        }
        return null
    }
    
    fun validateEmail(email: String?): String? {
        if (email.isNullOrEmpty()) {
            return null // Email is optional
        }
        if (!email.contains("@")) {
            return "Invalid email format"
        }
        return null
    }
    
    fun validatePhone(phone: String?): String? {
        if (phone.isNullOrEmpty()) {
            return null // Phone is optional
        }
        val cleanPhone = phone.replace("+", "").replace("-", "").replace(" ", "")
        if (!cleanPhone.all { it.isDigit() }) {
            return "Phone number can only contain digits, +, -, and spaces"
        }
        return null
    }
    
    fun validatePassword(password: String): String? {
        if (password.isEmpty()) {
            return "Password is required"
        }
        if (password.length < 6) {
            return "Password must be at least 6 characters"
        }
        return null
    }
    
    fun validateUsername(username: String): String? {
        if (username.isEmpty()) {
            return "Username is required"
        }
        if (username.length < 3) {
            return "Username must be at least 3 characters"
        }
        return null
    }
    
    fun validateRole(role: String): String? {
        val validRoles = listOf("BACKOFFICER", "OWNER", "OPERATOR")
        if (!validRoles.contains(role)) {
            return "Invalid role. Must be one of: ${validRoles.joinToString(", ")}"
        }
        return null
    }
}
