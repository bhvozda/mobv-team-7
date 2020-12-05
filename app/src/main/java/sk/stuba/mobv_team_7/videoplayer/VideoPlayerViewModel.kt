package sk.stuba.mobv_team_7.videoplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoPlayerViewModel : ViewModel() {
    // TODO: Implement the ViewModel

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
}