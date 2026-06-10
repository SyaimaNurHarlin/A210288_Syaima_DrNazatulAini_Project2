package com.example.a210288_syaima_drnazatulaini_project2.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a210288_syaima_drnazatulaini_project2.data.UserAccount
import com.example.a210288_syaima_drnazatulaini_project2.data.LunaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

// Pass the repository into the constructor
class LunaViewModel(private val repository: LunaRepository) : ViewModel() {

    // Simpan ID user yang tengah login
    private val _currentUserId = MutableStateFlow<Int?>(null)

    // --- NEW: WEB API STATE ---
    // This state variable will store the live advice string fetched from the internet
    var adviceText by mutableStateOf("Loading daily health tips...")
        private set

    // --- NEW: ACCELEROMETER HARDWARE SENSOR STATE ---
    // This tracks the live movement data coming from your SensorScreen!
    var activitySteps by mutableStateOf(0)
        private set

    // 1. Convert the Room Flow into a StateFlow for the UI
    // This replaces 'registeredUser'. Whenever Room updates, this updates.
    // KUNCI UTAMA: userAccount akan berubah ikut siapa yang Login
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val userAccount: StateFlow<UserAccount?> = _currentUserId
        .flatMapLatest { id ->
            if (id == null) {
                // Kalau belum login, bagi null
                flowOf(null)
            } else {
                // Kalau dah login, panggil data user secara real-time
                repository.getUserById(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Derived states for easier UI access
    var predictedDate by mutableStateOf<LocalDate?>(null)
        private set

    // --- ACCELEROMETER LOGIC METHODS ---
    fun incrementActivitySteps() {
        activitySteps++
    }

    fun resetActivitySteps() {
        activitySteps = 0
    }

    // --- NEW: FETCH ADVICE FUNCTION ---
    // This calls the Web API through the repository
    fun getDailyAdvice() {
        viewModelScope.launch {
            adviceText = repository.fetchDailyAdvice()
        }
    }

    fun registerUser(name: String, email: String, pass: String) {
        viewModelScope.launch {
            val newUser = UserAccount(name = name, email = email, password = pass)
            repository.insertUser(newUser)
        }
    }

    fun updatePeriodDate(date: LocalDate) {
        val current = userAccount.value ?: return
        viewModelScope.launch {
            repository.insertUser(current.copy(periodStartDate = date))
            predictedDate = date.plusDays(28)
        }
    }

    fun updateSleep(hours: Float) {
        val current = userAccount.value ?: return
        viewModelScope.launch {
            repository.insertUser(current.copy(sleepHours = hours))
        }
    }

    fun incrementWater() {
        val current = userAccount.value ?: return
        if (current.waterCount < 8) {
            viewModelScope.launch {
                repository.insertUser(current.copy(waterCount = current.waterCount + 1))
            }
        }
    }

    fun resetWater() {
        val current = userAccount.value ?: return
        viewModelScope.launch {
            repository.insertUser(current.copy(waterCount = 0))
        }
    }

    fun updateSymptom(newSymptom: List<String>) {
        val current = userAccount.value ?: return
        viewModelScope.launch {
            repository.insertUser(current.copy(lastSymptom = newSymptom))
        }
    }

    // Login logic: Checks against the current database user
    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.findUserByEmail(email)
            if (user != null && user.password == pass) {
                _currentUserId.value = user.uid // SET ID DI SINI
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    // Add this function inside LunaViewModel.kt
    fun deleteAccountAndLogout(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUser()     // Wipes the local Room database user_table!
            _currentUserId.value = null // Clears active session ID
            resetActivitySteps()        // Resets current live sensor steps
            onComplete()                // Navigates back to Login/Welcome screen
        }
    }

    // --- INTEGRASI STRATEGIK: ROOM + FIREBASE + WEB API ---
    fun saveWorkoutSession() {
        val current = userAccount.value ?: return
        val stepsToSave = activitySteps // Cache the active sensor steps before wiping it out

        viewModelScope.launch {
            try {
                // Create a single, updated data snapshot
                // We add our new sensor data to whatever step total is already saved in the database
                val updatedUser = current.copy(
                    activitySteps = current.activitySteps + stepsToSave
                )

                // This passes our single data object down to the repository pipeline.
                // repository automatically writes this updated object to Room (Local)
                // and pushes it straight to Firebase Cloud Document storage simultaneously!
                repository.insertUser(updatedUser)

                // Fetch new health advice from Web REST API to refresh UI state
                getDailyAdvice()

                // Reset temporary physical tracking buffer back to 0 for subsequent exercises
                resetActivitySteps()

            } catch (e: Exception) {
                android.util.Log.e("LunaDataFlow", "Data synchronization sequence broken: ${e.message}")
            }
        }
    }

    fun logoutUser(onLogoutComplete: () -> Unit) {
        // 1. Kosongkan ID sesi pengguna yang sedang aktif
        _currentUserId.value = null

        // 2. Set semula pembilang langkah sensor fizikal kembali ke 0
        resetActivitySteps()

        // 3. Jalankan rantaian navigasi untuk kembali ke skrin Login
        onLogoutComplete()
    }
}