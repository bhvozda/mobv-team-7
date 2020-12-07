package sk.stuba.mobv_team_7.network

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import sk.stuba.mobv_team_7.database.DatabasePost
import sk.stuba.mobv_team_7.home.PostDto

@JsonClass(generateAdapter = true)
data class NetworkPostContainer(val posts: List<PostNetwork>)

/**
 * Videos represent a devbyte that can be played.
 */
@JsonClass(generateAdapter = true)
data class PostNetwork(
    @SerializedName("postid")
    val postId: String,
    @SerializedName("created")
    var createdAt: String,
    @SerializedName("videourl")
    var videoUrl: String,
    @SerializedName("username")
    var username: String,
    @SerializedName("profile")
    var profile: String
)

/**
 * Convert Network results to database objects
 */
fun NetworkPostContainer.asDomainModel(): List<PostDto> {
    return posts.map {
        PostDto(
            postid = it.postId,
            created = it.createdAt,
            videourl = it.videoUrl,
            username = it.username,
            profile = it.profile)
    }
}


/**
 * Convert Network results to database objects
 */
fun NetworkPostContainer.asDatabaseModel(): List<DatabasePost> {
    return posts.map {
        DatabasePost(
            postId = it.postId,
            createdAt = it.createdAt,
            videoUrl = it.videoUrl,
            username = it.username,
            profile = it.profile)
    }
}