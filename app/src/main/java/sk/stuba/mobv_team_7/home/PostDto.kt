package sk.stuba.mobv_team_7.home

import java.util.*

data class PostDto(
    val postId: String,
    val createdAt: Date,
    val videoUrl: String,
    val username: String,
    val profile: String
)