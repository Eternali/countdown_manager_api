package org.example.countdownmanagerapi

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Countdown(
    val id: String,
    val owner: String?,
    val name: String?,
    val gradient: List<String?>?,
    val date: Date?
) {
    // Only for interacting with the database
    @JsonProperty
    var _id: String = ""
    get() = id
}
