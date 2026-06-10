package com.example.a210288_syaima_drnazatulaini_project2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import kotlin.collections.List

@Entity(tableName = "user_table")
data class UserAccount(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0, // Required for Room to track unique entries

    val name: String = "",
    val email: String = "",
    val password: String = "",

    val lastSymptom: List<String> = emptyList(),
    val sleepHours: Float = 7.5f,
    val waterCount: Int = 0, // Added to persist your hydration data
    val periodStartDate: LocalDate? = null,

    val activitySteps: Int = 0
)