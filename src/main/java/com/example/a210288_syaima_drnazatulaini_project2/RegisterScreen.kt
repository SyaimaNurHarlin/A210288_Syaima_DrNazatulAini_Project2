package com.example.a210288_syaima_drnazatulaini_project2.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a210288_syaima_drnazatulaini_project2.viewmodel.LunaViewModel

@Composable
fun RegisterScreen(vm: LunaViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var n by remember { mutableStateOf("") }
    var e by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    /* remember states untuk name, email, password . bila user click button
    strings value akan dipassed kepada viewModel */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Join LunaLog",
            style = MaterialTheme.typography.displaySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Start your health journey today",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = n, onValueChange = { n = it },
            label = { Text("Full Name") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = e, onValueChange = { e = it },
            label = { Text("Email") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = p, onValueChange = { p = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(32.dp))

        // Sign Up Button
        Button(
            onClick = {
                if (n.isNotEmpty() && e.isNotEmpty() && p.isNotEmpty()) {
                    vm.registerUser(n, e, p) // This triggers the Room insert via ViewModel
                    Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                    onBack() // Navigate back to Login
                } else {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Sign Up", fontSize = 18.sp)
        }

        TextButton(onClick = onBack) {
            Text("Already have an account? Log in", color = MaterialTheme.colorScheme.outline)
        }
    }
}