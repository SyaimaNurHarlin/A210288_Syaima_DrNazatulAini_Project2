package com.example.a210288_syaima_drnazatulaini_project2.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a210288_syaima_drnazatulaini_project2.R
import com.example.a210288_syaima_drnazatulaini_project2.ui.theme.BreathingCircleActive
import com.example.a210288_syaima_drnazatulaini_project2.ui.theme.BreathingCircleIdle
import com.example.a210288_syaima_drnazatulaini_project2.viewmodel.LunaViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    lunaViewModel: LunaViewModel,
    navController: NavController,
    onLog: () -> Unit,
    onProfile: () -> Unit,
    onInsights: () -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 1. Observe the database state!
    val userData by lunaViewModel.userAccount.collectAsState()

    LaunchedEffect(Unit) {
        lunaViewModel.getDailyAdvice() // This forces the API to trigger immediately when the screen loads
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // --- NEW DRAWER HEADER ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Column {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(12.dp))
                        // Pulling name from Room database
                        Text(
                            text = userData?.name ?: "Guest User",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userData?.email ?: "No email set",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                // --------------------------

                Spacer(Modifier.height(12.dp))
                Text(
                    "LunaLog Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )

                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onProfile() },
                    icon = { Icon(Icons.Default.Person, null) }
                )

                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; onLogout() },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("LUNALOG", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null) } }
                )
            }

        ) { padding ->
            Column(Modifier.padding(padding).padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Today, ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}", color = Color.Gray)
                DynamicDateStrip()

                ElevatedCard(Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {

                    Column(Modifier.padding(16.dp)) {
                        Text("Hi, $userName! ✨", fontWeight = FontWeight.Bold, fontSize = 20.sp)

                        // 2. Use userData from the database
                        val displaySymptoms = if (userData?.lastSymptom.isNullOrEmpty()) {
                            "None logged"
                        } else {
                            userData?.lastSymptom?.joinToString(", ")
                        }
                        Text("Recent Symptoms: $displaySymptoms", style = MaterialTheme.typography.bodyMedium)

                        // --- NEW: Web API Daily Advice Row ---
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp).padding(top = 2.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = lunaViewModel.adviceText, // Dynamically pulled from internet!
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                lineHeight = 18.sp
                            )
                        }

                        // Prediction Section
                        if (userData?.periodStartDate != null) { // Check database date
                            Spacer(Modifier.height(12.dp))
                            Card(
                                Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFAD1457), modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text("Next Cycle Prediction", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFAD1457))
                                        // Use the prediction logic
                                        val formattedDate = userData?.periodStartDate?.plusDays(28)?.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                                        Text("$formattedDate", fontSize = 14.sp, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // 3. Update the trackers to use database values
                SleepTrackerCard(
                    hours = userData?.sleepHours ?: 0f, // From Database
                    onClick = { navController.navigate("sleep_log") }
                )
                WaterTrackerCard(
                    count = userData?.waterCount ?: 0, // From Database
                    onAdd = { lunaViewModel.incrementWater() },
                    onReset = { lunaViewModel.resetWater()}
                )

                BreathingExerciseCard()
                CycleTrendGraph()

                Button(
                    onClick = { navController.navigate("period_log") }, // This triggers the navigation
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)) // Period Pink
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Log Period Start Date")
                }

                Button(onClick = onLog, Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Add Daily Log") }
                TextButton(onClick = onInsights, Modifier.align(Alignment.CenterHorizontally)) { Text("View Insights") }
            }
        }
    }
}


@Composable
fun DynamicDateStrip() {
    val today = LocalDate.now()
    Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        for (i in -2..2) {
            val date = today.plusDays(i.toLong())
            val active = i == 0
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(date.format(DateTimeFormatter.ofPattern("E")), fontSize = 10.sp, color = if(active) MaterialTheme.colorScheme.primary else Color.Gray)
                Box(Modifier.size(40.dp).background(if(active) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape), contentAlignment = Alignment.Center) {
                    Text(date.dayOfMonth.toString(), color = if(active) Color.White else Color.Black)
                }
            }
        }
    }
}

@Composable
fun SleepTrackerCard(hours: Float, onClick: () -> Unit) {
    val goal = 8f
    val progress = (hours / goal).coerceIn(0f, 1f)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { onClick() }, // Navigate to log screen
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Sleep Tracker", fontWeight = FontWeight.Bold)
                Text(if (hours > 0) "$hours hours slept" else "Tap to log sleep", fontSize = 14.sp)
            }
            // The progress bar now reflects actual data
            CircularProgressIndicator(
                progress = { progress },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun CycleTrendGraph() {
    ElevatedCard(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Cycle Trends", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth().height(60.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf(0.4f, 0.8f, 0.6f, 0.9f, 0.5f).forEach { h ->
                    Box(Modifier.width(24.dp).fillMaxHeight(h).background(MaterialTheme.colorScheme.primary.copy(0.6f), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                }
            }
        }
    }
}

@Composable
fun WaterTrackerCard(
    count: Int,
    onAdd: () -> Unit,
    onReset: () -> Unit // Tambah callback baru di sini
) {
    ElevatedCard(
        Modifier.fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Hydration", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                Text("$count / 8 glasses today", fontSize = 14.sp)
            }

            // Butang Reset
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.Refresh, // Guna ikon Refresh
                    contentDescription = "Reset Water",
                    tint = Color.Gray // Warna kelabu sikit supaya tak keliru dengan Add
                )
            }

            // Butang Add
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Add Water",
                    tint = Color(0xFF1976D2)
                )
            }
        }
    }
}

@Composable
fun BreathingExerciseCard() {
    var isActive by remember { mutableStateOf(false) } // toggle between "tap to breathe" and "mindful breathing"

    // looping animation for  breathing circle
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val size by infiniteTransition.animateValue(
        initialValue = 60.dp,
        targetValue = if (isActive) 100.dp else 60.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "size"
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { isActive = !isActive },
        colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(
                    id = if (isActive) R.drawable.bg_active else R.drawable.bg_idle
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            // UI CONTENT LAYER
            Column(
                Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isActive) "Mindful Breathing" else "Tap to Breathe",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = Color.White
                )

                Spacer(Modifier.height(20.dp))

                Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    if (isActive) {
                        Box(
                            Modifier
                                .size(size + 10.dp)
                                .background(BreathingCircleActive.copy(0.20f), CircleShape)
                        )
                    }

                    Box(
                        Modifier
                            .size(if (isActive) size else 60.dp)
                            .background(
                                if (isActive) BreathingCircleActive else BreathingCircleIdle,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isActive) {
                            Text(
                                text = if (size > 80.dp) "Inhale" else "Exhale",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                if (isActive) {
                    Text(
                        text = "Focus on the rhythm...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}