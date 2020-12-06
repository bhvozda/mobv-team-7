package sk.stuba.mobv_team_7.api

import com.google.gson.annotations.SerializedName

data class UserInfoRequest(

    @SerializedName("action")
    var action: String,
    @SerializedName("apikey")
    var apikey: String,
    @SerializedName("token")
    var token: String
)

data class UserInfoResponse(
    @SerializedName("token")
    var token: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("username")
    var username: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("profile")
    var profile: String
)

data class PostRequest(
    @SerializedName("apikey")
    var apikey: String,
    @SerializedName("token")
    var token: String
)

data class PostResponse(
    @SerializedName("status")
    var status: String
)