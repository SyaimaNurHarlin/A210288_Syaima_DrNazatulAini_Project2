package com.example.a210288_syaima_drnazatulaini_project2.data

import com.google.gson.annotations.SerializedName

data class AdviceResponse(
    @SerializedName("slip") val slip: Slip
)

data class Slip(
    @SerializedName("id") val id: Int,
    @SerializedName("advice") val advice: String
)