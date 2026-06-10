package com.example.a210288_syaima_drnazatulaini_project2.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.a210288_syaima_drnazatulaini_project2.R

// Define the Font Families based on your files
val AbrilFatface = FontFamily(
    Font(R.font.abril_fatface_regular, FontWeight.Normal)
)

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)

val Typography = Typography(
    // Tajuk besar "LUNALOG" uses Abril Fatface
    displayLarge = TextStyle(
        fontFamily = AbrilFatface,
        fontWeight = FontWeight.Normal, // Abril Fatface is naturally heavy
        fontSize = 32.sp,
        letterSpacing = 1.sp
    ),
    // "Welcome Back" uses Montserrat Bold
    headlineMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    // Teks biasa uses Montserrat Regular
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Label kecil uses Montserrat Regular/Medium
    labelMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)