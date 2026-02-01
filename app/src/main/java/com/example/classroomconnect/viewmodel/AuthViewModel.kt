package com.example.classroomconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.classroomconnect.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()
    private val _signupResult = MutableLiveData<Pair<Boolean, String?>>()
    val signupResult: LiveData<Pair<Boolean, String?>> = _signupResult
    fun signUp(
        email: String,
        password: String,
        name: String,
        role: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || role.isEmpty()) {
            _signupResult.value = Pair(false, "All fields are required")
            return
        }
        if (password.length < 6) {
            _signupResult.value = Pair(false, "Password must be at least 6 characters")
            return
        }
        repo.signUpWithEmail(email, password, name, role) { success, error ->
            _signupResult.postValue(Pair(success, error))
        }
    }
    fun signIn(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit) {
        repo.signIn(email, password) { success, error ->
            onResult(success, error)
        }
    }
}
