package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.UserEntity
import com.example.data.models.UserRole
import com.example.data.repository.AquaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AquaRepository
) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
    val currentRole: StateFlow<UserRole> = repository.currentRole

    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun selectRole(role: UserRole) {
        repository.switchRole(role)
    }

    fun loginWithEmail(email: String, isSeller: Boolean) {
        viewModelScope.launch {
            if (email.isBlank() || !email.contains("@")) {
                _authError.value = "Please enter a valid email address"
                return@launch
            }
            _authError.value = null
            if (isSeller) {
                repository.loginAsSeller("seller_1")
            } else {
                repository.loginAsCustomer(email)
            }
        }
    }

    fun sendOtp(phone: String) {
        if (phone.length < 8) {
            _authError.value = "Please enter a valid phone number"
            return
        }
        _authError.value = null
        _otpSent.value = true
    }

    fun verifyOtp(otp: String, isSeller: Boolean) {
        if (otp.length < 4) {
            _authError.value = "Enter 4-digit code (Use 1234)"
            return
        }
        viewModelScope.launch {
            _authError.value = null
            if (isSeller) {
                repository.loginAsSeller("seller_1")
            } else {
                repository.loginAsCustomer("user_otp@example.com")
            }
            _otpSent.value = false
        }
    }

    fun logout() {
        repository.logout()
        _otpSent.value = false
    }

    fun clearError() {
        _authError.value = null
    }
}
