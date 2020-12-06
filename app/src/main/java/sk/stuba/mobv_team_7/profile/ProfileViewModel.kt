package sk.stuba.mobv_team_7.profile

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
import sk.stuba.mobv_team_7.api.PhotoUpdateRequest
import sk.stuba.mobv_team_7.api.PhotoUpdateResponse
import java.io.File

class ProfileViewModel : ViewModel() {

    private var _postStatus = MutableLiveData<PhotoUpdateResponse>()
    val postStatus: LiveData<PhotoUpdateResponse>
        get() = _postStatus

    init {
        _postStatus.value = null
    }

    fun postNewPost(profile: PhotoUpdateRequest, file: File) {

        val image = MultipartBody.Part.createFormData(
            "image",
            file.name,
            RequestBody.create(MediaType.parse("image/jpeg"), file)
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

        val api = GithubApi.retrofitService.photoUpdate(image, data)

        api.enqueue(object : Callback<PhotoUpdateResponse> {
            override fun onFailure(call: Call<PhotoUpdateResponse>, t: Throwable) {
                Log.d("TAG_TAG", "Failed :" + t.message)
            }

            override fun onResponse(call: Call<PhotoUpdateResponse>, response: Response<PhotoUpdateResponse>) {
                _postStatus.value = response.body()
            }
        })
    }
}