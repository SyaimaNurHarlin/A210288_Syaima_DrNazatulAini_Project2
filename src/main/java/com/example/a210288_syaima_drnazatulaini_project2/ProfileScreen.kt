package com.example.a210288_syaima_drnazatulaini_project2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a210288_syaima_drnazatulaini_project2.viewmodel.LunaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    name: String,
    vm: LunaViewModel,
    onBack: () -> Unit,
    onNavigateToSensor: () -> Unit, // 1. Added a callback parameter for navigation
    onNavigateToLogin: () -> Unit // 2. Added navigation callback to go back to Log in screen
) {
    val pastelPinkBg = Color(0xFFFDF5F7)
    val cardPink = Color(0xFFFCE4EC)
    val darkBrownText = Color(0xFF3E2723)
    val softBrownText = Color(0xFF5D4037)
    val accentPink = Color(0xFFF8BBD0)

    Scaffold(
        containerColor = pastelPinkBg,
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(40.dp))

            // --- PROFILE AVATAR CIRCLE (DARK BROWN & PASTEL) ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(cardPink, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = darkBrownText
                )
            }

            Spacer(Modifier.height(20.dp))

            // --- USER TYPOGRAPHY (DARK BROWN) ---
            Text(
                text = name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = darkBrownText
            )
            Text(
                text = "Active Member",
                fontSize = 14.sp,
                color = softBrownText,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(40.dp))

            // --- CARD 1: SUBSCRIPTION ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardPink)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = darkBrownText, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Subscription", fontWeight = FontWeight.Bold, color = darkBrownText, fontSize = 16.sp)
                        Text("LunaLog Premium", color = softBrownText, fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- CARD 2: ACCELEROMETER DIAGNOSTICS ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSensor() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardPink)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DirectionsRun, null, tint = darkBrownText, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Daily Step Tracker", fontWeight = FontWeight.Bold, color = darkBrownText, fontSize = 16.sp)
                        Text("Click to turn on activity tracker", color = softBrownText, fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp)) // Adds space before the logout button

            // 🌟 ADDED: YOUR LOGOUT BUTTON PLACED SECURELY INSIDE THE LAYOUT COLUMN 🌟
            Button(
                onClick = {
                    vm.logoutUser {
                        onNavigateToLogin()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC62828) // Crimson Red for sign-out visual cue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

}