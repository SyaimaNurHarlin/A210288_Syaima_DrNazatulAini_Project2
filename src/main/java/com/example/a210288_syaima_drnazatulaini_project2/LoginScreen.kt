package com.example.a210288_syaima_drnazatulaini_project2.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a210288_syaima_drnazatulaini_project2.R
import com.example.a210288_syaima_drnazatulaini_project2.viewmodel.LunaViewModel

@Composable
fun LoginScreen(vm: LunaViewModel, onSuccess: () -> Unit, onReg: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo with Gradient
        Box(
            Modifier.size(120.dp).background(
                brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondaryContainer)),
                shape = CircleShape
            ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_luna),
                contentDescription = null,
                modifier = Modifier.size(120.dp).clip(CircleShape)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("Welcome to LunaLog", style = MaterialTheme.typography.headlineMedium, fontFamily = FontFamily.Serif, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(32.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(pass, { pass = it }, label = { Text("Password") }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

        Spacer(Modifier.height(32.dp))

        // Login Button with Room Database logic
        Button(
            onClick = {
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                } else {
                    // Panggil login dengan 'callback' { success -> ... }
                    vm.login(email, pass) { success ->
                        if (success) {
                            onSuccess()
                        } else {
                            Toast.makeText(context, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Login", fontSize = 18.sp)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onReg) {
            Text("Create an account", color = MaterialTheme.colorScheme.secondary)
        }
    }
}