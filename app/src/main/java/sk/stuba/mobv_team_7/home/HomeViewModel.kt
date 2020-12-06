package sk.stuba.mobv_team_7.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.stuba.mobv_team_7.api.*
import java.io.File


class HomeViewModel : ViewModel() {

    private var _userData = MutableLiveData<GithubUser>()
    val userData: LiveData<GithubUser>
        get() = _userData
    private var _userInfo = MutableLiveData<UserInfoResponse>()
    val userInfo: LiveData<UserInfoResponse>
        get() = _userInfo
    private var _postStatus = MutableLiveData<PostResponse>()
    val postStatus: LiveData<PostResponse>
        get() = _postStatus

    init {
        _userData.value = null
        _userInfo.value = null
        _postStatus.value = null
    }

    fun getUserData(profile: String) {
        val api = GithubApi.retrofitService.getUserData(profile)

        api.enqueue(object : Callback<GithubUser> {
            override fun onFailure(call: Call<GithubUser>, t: Throwable) {
                Log.d("TAG_TAG", "Failed :" + t.message)
            }

            override fun onResponse(call: Call<GithubUser>, response: Response<GithubUser>) {
                _userData.value = response.body()
            }
        })
    }

    fun getUserInfo(profile: UserInfoRequest) {
        val api = GithubApi.retrofitService.getUserInfo(profile)

        api.enqueue(object : Callback<UserInfoResponse> {
            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.d("TAG_TAG", "Failed :" + t.message)
            }

            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                _userInfo.value = response.body()
            }
        })
    }

    fun postNewPost(profile: PostRequest, file: File) {

        val video = MultipartBody.Part.createFormData(
            "video",
            file.name,
            RequestBody.create(MediaType.parse("image/*"), file)
        )
        val data = MultipartBody.Part.createFormData(
            "data",
            file.name,
            RequestBody.create(MediaType.parse("application/json"), profile.toString())
        )

        val api = GithubApi.retrofitService.postPost(video, data)

        api.enqueue(object : Callback<PostResponse> {
            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                Log.d("TAG_TAG", "Failed :" + t.message)
            }

            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                _postStatus.value = response.body()
            }
        })
    }
}