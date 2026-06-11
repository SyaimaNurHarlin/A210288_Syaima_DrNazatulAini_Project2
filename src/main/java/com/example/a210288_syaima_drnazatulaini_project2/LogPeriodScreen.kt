package com.example.a210288_syaima_drnazatulaini_project2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogPeriodScreen(onSave: (LocalDate) -> Unit, onBack: () -> Unit) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Convert timestamp to LocalDate
    val selectedDate = datePickerState.selectedDateMillis?.let {
        java.time.Instant.ofEpochMilli(it)
            .atZone(java.time.ZoneId.of("UTC")) // DatePickers usually return UTC millis
            .withZoneSameInstant(java.time.ZoneId.systemDefault())
            .toLocalDate()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Period") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFFE91E63))

            Text("When did your last period start?", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedDate?.toString() ?: "Select Date")
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("OK") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { selectedDate?.let { onSave(it) } },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedDate != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)) // Period Pink
            ) {
                Text("Save and Predict")
            }
        }
    }
}