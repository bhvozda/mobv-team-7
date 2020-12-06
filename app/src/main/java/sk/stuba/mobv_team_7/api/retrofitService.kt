package sk.stuba.mobv_team_7.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


private const val BASE_URL = "https://api.github.com/"
//"https://api.github.com/users/Xsimekj1"
private const val URL = "http://api.mcomputing.eu/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(URL)
    .build()

interface GithubApiService {
    @GET("/users/{profile}")
    fun getUserData(@Path("profile") profile: String):
            Call<GithubUser>

    @Headers("Content-Type: application/json")
    @POST("/mobv/service.php")
    fun getUserInfo(@Body body: UserInfoRequest):
            Call<UserInfoResponse>

    @Multipart
    @POST("/mobv/post.php")
    fun postPost(
        @Part video: MultipartBody.Part?,
        @Part data: MultipartBody.Part?
    ):
            Call<PostResponse>
}

object GithubApi {
    val retrofitService: GithubApiService by lazy {
        retrofit.create(GithubApiService::class.java)
    }
}