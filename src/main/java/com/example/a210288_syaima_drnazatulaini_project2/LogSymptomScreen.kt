package com.example.a210288_syaima_drnazatulaini_project2.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSymptomScreen(current: List<String>, onSave: (List<String>) -> Unit, onBack: () -> Unit) {
    val selectedSymptoms = remember { mutableStateListOf<String>().apply { addAll(current) } }

    val symptomOptions = listOf("Fine", "Cramps", "Migraine", "Fatigue", "Calm","Vaginal Itching","Bloating","Constipation")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Symptoms") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { p ->
        Column(Modifier.padding(p).padding(24.dp)) {
            Text("How are you feeling? (Select all that apply)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            symptomOptions.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedSymptoms.contains(item)) selectedSymptoms.remove(item)
                            else selectedSymptoms.add(item)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedSymptoms.contains(item),
                        onCheckedChange = { isChecked ->
                            if (isChecked) selectedSymptoms.add(item)
                            else selectedSymptoms.remove(item)
                        }
                    )
                    Text(item, Modifier.padding(start = 12.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onSave(selectedSymptoms.toList()) },
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Symptoms")
            }
        }
    }
}