package sk.stuba.mobv_team_7.home

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class PostDto(
    val postid: String,
    val created: String,
    val videourl: String,
    val username: String,
    val profile: String
)