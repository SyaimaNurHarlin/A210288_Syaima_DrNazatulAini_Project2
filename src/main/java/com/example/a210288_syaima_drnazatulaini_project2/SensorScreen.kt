package com.example.a210288_syaima_drnazatulaini_project2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a210288_syaima_drnazatulaini_project2.viewmodel.LunaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreen(vm: LunaViewModel, onBack: () -> Unit) { // Notice the parameter name is 'vm'
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val motionSensor = com.example.a210288_syaima_drnazatulaini_project2.sensor.AccelerometerMonitor(context) {
            vm.incrementActivitySteps()
        }
        motionSensor.startListening()

        onDispose {
            motionSensor.stopListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Motion Tracker") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Move or walk around to see your wellness activity update live!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Dynamic Sensor Counter Ring
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${vm.activitySteps}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Motion Units", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { vm.resetActivitySteps() },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Reset")
                }

                Button(
                    onClick = {
                        // Memicu rantaian data: Room local update + Firebase Cloud push + Retrofit API fetch
                        vm.saveWorkoutSession()

                        // Pulang semula ke skrin Profile dengan selamat
                        onBack()
                    },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text("Save & Complete Session")
                }
            }
        }
    }
}