package sk.stuba.mobv_team_7.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import sk.stuba.mobv_team_7.home.PostDto
import java.util.*


@Entity
data class DatabasePost constructor(
    @PrimaryKey
    val postId: String,
    val createdAt: Date,
    val videoUrl: String,
    val username: String,
    val profile: String)

/**
 * Map DatabasePosts to domain entities
 */
fun List<DatabasePost>.asDomainModel(): List<PostDto> {
    return map {
        PostDto(
            postId = it.postId,
            createdAt = it.createdAt,
            videoUrl = it.videoUrl,
            username = it.username,
            profile = it.profile)
    }
}