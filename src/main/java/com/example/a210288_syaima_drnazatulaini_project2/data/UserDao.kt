package com.example.a210288_syaima_drnazatulaini_project2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // 1. Insert or Update: If the user ID already exists, it replaces the data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount): Long

    // 2. Read: This returns a Flow.
    // Whenever the database changes, the Flow will emit the new data automatically!
    @Query("SELECT * FROM user_table LIMIT 1")
    fun getUser(): Flow<UserAccount?>

    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccount?

    @Query("SELECT * FROM user_table WHERE uid = :id")
    fun getUserById(id: Int): Flow<UserAccount?>

    // 3. Delete: Useful for logout or clearing data
    @Query("DELETE FROM user_table")
    suspend fun clearAll(): Int

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWater(water: WaterIntake)

    // Sleep
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(sleep: SleepTracker)

    // Symptoms
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptom(symptom: LogSymptom)

    // Period
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: LogPeriodDate)*/
}