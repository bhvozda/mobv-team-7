package sk.stuba.mobv_team_7.home

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.database.getDatabase
import sk.stuba.mobv_team_7.http.API_KEY
import sk.stuba.mobv_team_7.repository.PostsRepository
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val postsRepository = PostsRepository(getDatabase(application))

    // The login finished event
    private val _eventBoarding = MutableLiveData<Boolean>()
    val eventBoarding: LiveData<Boolean>
        get() = _eventBoarding

    /** Methods for buttons presses **/
    fun onBoarding() {
        _eventBoarding.value = true
    }

    fun onBoardingComplete() {
        _eventBoarding.value = false
    }

    fun refreshDataFromRepository(user: User) {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("action", "posts")
            jsonObject.put("apikey", API_KEY)
            jsonObject.put("token", user.token)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsonObjectString = jsonObject.toString()

        viewModelScope.launch {
            try {
                postsRepository.refreshPosts(RequestBody.create(MediaType.parse("application/json"), jsonObjectString))
            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
//                if(playlist.value.isNullOrEmpty())
//                    _eventNetworkError.value = true
            }
        }
    }

}