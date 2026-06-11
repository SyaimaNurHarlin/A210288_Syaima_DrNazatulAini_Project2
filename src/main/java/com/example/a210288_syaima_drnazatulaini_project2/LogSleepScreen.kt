package com.example.a210288_syaima_drnazatulaini_project2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSleepScreen(currentHours: Float, onSave: (Float) -> Unit, onBack: () -> Unit) {
    var sleepAmount by remember { mutableFloatStateOf(currentHours.ifZero(7.5f)) }
    val quickOptions = listOf(6f, 7f, 8f, 9f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Sleep") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("How long did you sleep?", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(40.dp))

            // Large Visual Display
            Text(
                text = "${sleepAmount} hrs",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // Efficiency Part 1: Quick Select Chips
            Text("Quick Select", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 12.dp)) {
                quickOptions.forEach { hours ->
                    FilterChip(
                        selected = sleepAmount == hours,
                        onClick = { sleepAmount = hours },
                        label = { Text("${hours.toInt()}h") }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Efficiency Part 2: Precision Stepper (+ / -)
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledIconButton(
                    onClick = { if (sleepAmount > 0) sleepAmount -= 0.5f },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease") }

                Text(
                    "Adjust by 30 mins",
                    Modifier.padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.bodyLarge
                )

                FilledIconButton(
                    onClick = { if (sleepAmount < 24) sleepAmount += 0.5f }
                ) { Icon(Icons.Default.Add, contentDescription = "Increase") }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onSave(sleepAmount) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Log", fontSize = 18.sp)
            }
        }
    }
}

// Helper to set a default if 0
fun Float.ifZero(default: Float): Float = if (this <= 0f) default else this