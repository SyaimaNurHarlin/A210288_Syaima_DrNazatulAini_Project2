package com.example.a210288_syaima_drnazatulaini_project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.a210288_syaima_drnazatulaini_project2.data.LunaDatabase
import com.example.a210288_syaima_drnazatulaini_project2.data.LunaRepository
import com.example.a210288_syaima_drnazatulaini_project2.screens.*
import com.example.a210288_syaima_drnazatulaini_project2.ui.theme.A210288_Syaima_DrNazatulAini_Project2Theme
import com.example.a210288_syaima_drnazatulaini_project2.viewmodel.LunaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Room Database and DAO
        val database = LunaDatabase.getDatabase(this)
        val repository = LunaRepository(database.userDao())

        // 2. Initialize ViewModel with a custom Factory
        // This is necessary because LunaViewModel has a parameter (the repository)
        val lunaViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LunaViewModel(repository) as T
            }
        })[LunaViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            A210288_Syaima_DrNazatulAini_Project2Theme {
                LunaAppNavigation(lunaViewModel)
            }
        }
    }
}

@Composable
fun LunaAppNavigation(lunaViewModel: LunaViewModel) {
    val navController = rememberNavController()

    // 3. Observe the Room Data as State
    // Whenever the database changes, userData will trigger a UI recompose
    val userData by lunaViewModel.userAccount.collectAsState()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                lunaViewModel,
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onReg = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(lunaViewModel) { navController.popBackStack() }
        }

        composable("home") {
            HomeScreen(
                userName = userData?.name ?: "User",
                lunaViewModel = lunaViewModel,
                navController = navController,
                onLog = { navController.navigate("log") },
                onProfile = { navController.navigate("profile") },
                onInsights = { navController.navigate("insights") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        composable("period_log"){
            LogPeriodScreen(
                onSave = { selectedDate ->
                    lunaViewModel.updatePeriodDate(selectedDate)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("sleep_log") {
            LogSleepScreen(
                currentHours = userData?.sleepHours ?: 0f,
                onSave = { hours ->
                    lunaViewModel.updateSleep(hours)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("log") {
            LogSymptomScreen(
                current = userData?.lastSymptom ?: emptyList(),
                onSave = { symptomsList ->
                    lunaViewModel.updateSymptom(symptomsList)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            val userData by lunaViewModel.userAccount.collectAsState()
            ProfileScreen(
                name = userData?.name ?: "User",
                onBack = { navController.popBackStack() },
                onNavigateToSensor = { navController.navigate("sensor_workspace") } // Triggers the next jump!
            )
        }

        // --- DEDICATED SENSOR SCREEN ---
        composable("sensor_workspace") {
            SensorScreen(
                vm = lunaViewModel,
                onBack = { navController.popBackStack() } // Safely goes back to profile
            )
        }

        composable("insights") {
            InsightsScreen(onBack = { navController.popBackStack() })
        }
    }
}