package org.example.countdownmanagerapi

import java.util.*

data class Countdown(
    val id: String,
    val name: String,
    val gradient: List<String>,
    val date: Date)
