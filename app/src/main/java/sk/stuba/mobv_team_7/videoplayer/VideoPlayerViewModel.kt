package sk.stuba.mobv_team_7.videoplayer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.stuba.mobv_team_7.api.GithubApi
import sk.stuba.mobv_team_7.api.PostRequest
import sk.stuba.mobv_team_7.api.PostResponse
import java.io.File

class VideoPlayerViewModel : ViewModel() {

    private var _postStatus = MutableLiveData<PostResponse>()
    val postStatus: LiveData<PostResponse>
        get() = _postStatus

    init {
        _postStatus.value = null
    }

    // The login finished event
    private val _eventOpenPost = MutableLiveData<Boolean>()
    val eventOpenPost: LiveData<Boolean>
        get() = _eventOpenPost

    /** Methods for buttons presses **/
    fun onPostOpen() {
        //TODO Do the login
        _eventOpenPost.value = true
    }

    fun onPostOpenComplete() {
        _eventOpenPost.value = false
    }

    fun postNewPost(profile: PostRequest, file: File) {

        val video = MultipartBody.Part.createFormData(
            "video",
            file.name,
            RequestBody.create(MediaType.parse("video/mp4"), file)
        )

        val jsonObject = JSONObject()
        try {
            jsonObject.put("apikey", profile.apikey)
            jsonObject.put("token", profile.token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val data = MultipartBody.Part.createFormData(
            "data",
            jsonObject.toString()
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