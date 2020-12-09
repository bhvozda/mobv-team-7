package sk.stuba.mobv_team_7.profile

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Job
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
import sk.stuba.mobv_team_7.http.API_KEY
import sk.stuba.mobv_team_7.http.URL
import java.io.File

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _eventGalleryPictureUpload = MutableLiveData<Boolean>()
    val eventGalleryPictureUpload: LiveData<Boolean>
        get() = _eventGalleryPictureUpload

    private val _eventUserUpdated = MutableLiveData<String>()
    val eventUserUpdated: LiveData<String>
        get() = _eventUserUpdated

    private val _eventUserPictureDeleted = MutableLiveData<Boolean>()
    val eventUserPictureDeleted: LiveData<Boolean>
        get() = _eventUserPictureDeleted

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

        api.enqueue(object : Callback<PhotoUpdateResponse> {
            override fun onFailure(call: Call<PhotoUpdateResponse>, t: Throwable) {
                Log.d("TAG_TAG", "Failed :" + t.message)
                Toast.makeText(
                    getApplication(),
                    "Profile picture saving failed!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<PhotoUpdateResponse>,
                response: Response<PhotoUpdateResponse>
            ) {
                _postStatus.value = response.body()
                Toast.makeText(
                    getApplication(),
                    "Profile picture saved succesfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    fun getUpdatedUserData(token: String) {
        val queue = Volley.newRequestQueue(getApplication())

        val jsonRoot = JSONObject()
        jsonRoot.put("action", "userProfile")
        jsonRoot.put("apikey", API_KEY)
        jsonRoot.put("token", token)

        val jsonRequest = JsonObjectRequest(
            URL, jsonRoot,
            com.android.volley.Response.Listener { response ->
                _eventUserUpdated.value = response.get("profile").toString()
            },
            com.android.volley.Response.ErrorListener {
                // TODO: crashanlytics
                Toast.makeText(getApplication(), "Get user info not successful.", Toast.LENGTH_LONG)
                    .show()
            })
        queue.add(jsonRequest)
    }

    fun deleteUserPicture(token: String) {
        val queue = Volley.newRequestQueue(getApplication())

        val jsonRoot = JSONObject()
        jsonRoot.put("action", "clearPhoto")
        jsonRoot.put("apikey", API_KEY)
        jsonRoot.put("token", token)

        val jsonRequest = JsonObjectRequest(
            URL, jsonRoot,
            com.android.volley.Response.Listener {
                _eventUserPictureDeleted.value = true
                Toast.makeText(getApplication(), "Delete succesful.", Toast.LENGTH_LONG)
                    .show()
            },
            com.android.volley.Response.ErrorListener {
                // TODO: crashanlytics
                Toast.makeText(getApplication(), "Delete not succesful.", Toast.LENGTH_LONG)
                    .show()
            })
        queue.add(jsonRequest)
    }

    /** Methods for buttons presses **/
    fun onImageUpload() {
        _eventGalleryPictureUpload.value = true
    }

    fun onImageUploadComplete() {
        _eventGalleryPictureUpload.value = false
    }

    fun onImagePostComplete() {
        _postStatus.value = null
    }

    fun onGetUpdatedUserDataComplete() {
        _eventUserUpdated.value = ""
    }

    fun onDeleteUserPictureComplete() {
        _eventUserPictureDeleted.value = false
    }

}