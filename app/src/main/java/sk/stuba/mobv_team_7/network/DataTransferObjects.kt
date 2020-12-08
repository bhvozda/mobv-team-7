package sk.stuba.mobv_team_7.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import sk.stuba.mobv_team_7.database.DatabasePost

@JsonClass(generateAdapter = true)
data class NetworkPost(
    @Json(name = "postid")
    val postId: String,
    @Json(name = "created")
    var createdAt: String,
    @Json(name = "videourl")
    var videoUrl: String,
    @Json(name = "username")
    var username: String,
    @Json(name = "profile")
    var profile: String
)

fun List<NetworkPost>.asDatabaseModel(): List<DatabasePost> {
    return map {
        DatabasePost(
            postId = it.postId,
            createdAt = it.createdAt,
            videoUrl = it.videoUrl,
            username = it.username,
            profile = it.profile)
    }
}
