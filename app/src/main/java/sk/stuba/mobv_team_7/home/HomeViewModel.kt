package sk.stuba.mobv_team_7.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.database.getDatabase
import sk.stuba.mobv_team_7.http.API_KEY
import sk.stuba.mobv_team_7.repository.PostsRepository
import java.io.IOException

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var _eventPostsLoaded = MutableLiveData<Boolean>(false)
    val eventPostsLoaded: LiveData<Boolean>
        get() = _eventPostsLoaded

    private val postsRepository = PostsRepository(getDatabase(application))

    val postsList = postsRepository.posts

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
                _eventPostsLoaded.value = true
            } catch (networkError: IOException) {
                Toast.makeText(getApplication(), "Network error (failed to reload)", Toast.LENGTH_LONG).show()
                _eventPostsLoaded.value = true
            }
        }
    }

    /**
     * Resets the network error flag.
     */
    fun onPostsLoadedComplete() {
        _eventPostsLoaded.value = false
    }

}