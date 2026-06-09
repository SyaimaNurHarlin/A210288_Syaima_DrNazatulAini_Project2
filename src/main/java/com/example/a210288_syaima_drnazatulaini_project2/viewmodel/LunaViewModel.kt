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
    // Tambah parameter onResult supaya UI tahu login berjaya atau tidak
    // Login Logic - Sekarang dia akan update _currentUserId
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

    // --- INTEGRASI STRATEGIK: ROOM + FIREBASE + WEB API ---
    fun saveWorkoutSession() {
        val current = userAccount.value ?: return
        val stepsToSave = activitySteps // Pegang data sensor semasa sebelum di-reset

        viewModelScope.launch {
            try {
                // 1. LOCAL PERSISTENCE (ROOM DATABASE)
                // Menyimpan data kemas kini pengguna ke pangkalan data tempatan device.
                val updatedUser = current.copy(waterCount = current.waterCount)
                repository.insertUser(updatedUser)

                // 2. CLOUD INTEGRATION (GOOGLE FIREBASE FIRESTORE)
                // Menolak data sensor fizikal ke pelayan awan (Cloud) untuk penyegerakan akaun.
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val logPayload = hashMapOf(
                    "userId" to current.uid,
                    "userEmail" to current.email,
                    "recordedSteps" to stepsToSave,
                    "syncTimestamp" to com.google.firebase.Timestamp.now()
                )

                firestore.collection("luna_sensor_logs")
                    .add(logPayload)
                    .addOnSuccessListener {
                        android.util.Log.d("LunaFirebase", "Successfully sent data to Firestore Cloud!")
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("LunaFirebase", "Failed to sync to Cloud", e)
                    }

                // 3. DATA FROM INTERNET (RETROFIT WEB API)
                getDailyAdvice()

                // Kembalikan kaunter pergerakan sensor ke angka 0 untuk persediaan sesi baharu
                resetActivitySteps()

            } catch (e: Exception) {
                android.util.Log.e("LunaDataFlow", "Data save chain broken: ${e.message}")
            }
        }
    }
}