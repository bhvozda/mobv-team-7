package sk.stuba.mobv_team_7.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//class Post {
//    @SerializedName("title") var title: String? = null
//    @SerializedName("body") var body: String? = null
//    @SerializedName("userId") var userId: Int? = null
//    @SerializedName("id") var id: Int? = null
//}

data class Post (
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("user_email") val userEmail: String?,
    @SerializedName("user_age") val userAge: String?,
    @SerializedName("user_uid") val userUid: String?
)