package com.example.a210288_syaima_drnazatulaini_project2.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class LunaRepository(private val userDao: UserDao,
    private val apiService: ApiService = ApiService.create()) {

    // 1. Initialise Firestore
    private val firestore = FirebaseFirestore.getInstance()

    // Ambil data user sebagai stream (Flow)
    val userAccount: Flow<UserAccount?> = userDao.getUser()

    fun getUserById(id: Int): Flow<UserAccount?> {
        return userDao.getUserById(id)
    }

    suspend fun findUserByEmail(email: String): UserAccount? {
        return userDao.getUserByEmail(email)
    }

    // 2. Add or Update a user (Local + Cloud)
    suspend fun insertUser(user: UserAccount) {
        try {
            // A. Simpan ke Room (Local)
            userDao.insertUser(user)
            Log.d("LunaRepo", "Successfully saved to Room")

            // B. Simpan ke Firebase Firestore (Cloud)
            // Kita guna email sebagai ID unik supaya data tidak bertindih
            firestore.collection("users")
                .document(user.email)
                .set(user)
                .await() // Tunggu sehingga selesai hantar ke cloud

            Log.d("LunaRepo", "Successfully synced to Firebase Cloud!")

        } catch (e: Exception) {
            // Jika internet tiada, error akan ditangkap di sini
            // Tapi data tetap selamat dalam Room (Local)
            Log.e("LunaRepo", "Firebase Sync Failed: ${e.message}")
        }
    }

    // --- WATER INTAKE ---
    suspend fun insertWater(water: WaterIntake) {
        try {
            userDao.insertWater(water) // Save to Room
            firestore.collection("users")
                .document(water.userEmail) // Using email from the water object
                .collection("water_logs")
                .add(water)
                .await()
            Log.d("LunaRepo", "Water Synced")
        } catch (e: Exception) {
            Log.e("LunaRepo", "Water Sync Failed: ${e.message}")
        }
    }

    // --- SLEEP TRACKER ---
    suspend fun insertSleep(sleep: SleepTracker) {
        try {
            userDao.insertSleep(sleep)
            firestore.collection("users")
                .document(sleep.userEmail)
                .collection("sleep_logs")
                .add(sleep)
                .await()
            Log.d("LunaRepo", "Sleep Synced")
        } catch (e: Exception) {
            Log.e("LunaRepo", "Sleep Sync Failed: ${e.message}")
        }
    }

    // --- LOG SYMPTOM ---
    suspend fun insertSymptom(symptom: LogSymptom) {
        try {
            userDao.insertSymptom(symptom)
            firestore.collection("users")
                .document(symptom.userEmail)
                .collection("symptom_logs")
                .add(symptom)
                .await()
            Log.d("LunaRepo", "Symptom Synced")
        } catch (e: Exception) {
            Log.e("LunaRepo", "Symptom Sync Failed: ${e.message}")
        }
    }

    // --- LOG PERIOD DATE ---
    suspend fun insertPeriod(period: LogPeriodDate) {
        try {
            userDao.insertPeriod(period)
            firestore.collection("users")
                .document(period.userEmail)
                .collection("period_logs")
                .add(period)
                .await()
            Log.d("LunaRepo", "Period Synced")
        } catch (e: Exception) {
            Log.e("LunaRepo", "Period Sync Failed: ${e.message}")
        }
    }

    suspend fun deleteUser() {
        userDao.clearAll()
    }

    // --- FETCH WEB API DATA ---
    suspend fun fetchDailyAdvice(): String {
        return try {
            val response = apiService.getRandomAdvice()
            response.slip.advice
        } catch (e: Exception) {
            Log.e("LunaRepo", "API Fetch Failed: ${e.message}")
            "Stay hydrated and take care of yourself today!" // Fallback message if offline
        }
    }
}