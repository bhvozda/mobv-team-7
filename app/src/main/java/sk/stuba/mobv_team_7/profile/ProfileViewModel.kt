package sk.stuba.mobv_team_7.profile

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
import sk.stuba.mobv_team_7.data.User
import java.io.File

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = Job()

    private val _eventGalleryPictureUpload = MutableLiveData<Boolean>()
    val eventGalleryPictureUpload: LiveData<Boolean>
        get() = _eventGalleryPictureUpload

    private var _postStatus = MutableLiveData<PhotoUpdateResponse>()
    val postStatus: LiveData<PhotoUpdateResponse>
        get() = _postStatus

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

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

        viewModelScope.launch(Dispatchers.IO) {
            api.enqueue(object : Callback<PhotoUpdateResponse> {
                override fun onFailure(call: Call<PhotoUpdateResponse>, t: Throwable) {
                    Log.d("TAG_TAG", "Failed :" + t.message)
                    Toast.makeText(getApplication(), "Profile picture saving failed!", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<PhotoUpdateResponse>, response: Response<PhotoUpdateResponse>) {
                    _postStatus.value = response.body()
                    Toast.makeText(getApplication(), "Profile picture saved succesfully!", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    /** Methods for buttons presses **/
    fun onImageUpload() {
        _eventGalleryPictureUpload.value = true
    }

    fun onImageUploadComplete() {
        _eventGalleryPictureUpload.value = false
    }

    fun onImagePostCompleted() {
        _postStatus.value = null
    }
}