package sk.stuba.mobv_team_7.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostsViewModel(): ViewModel() {

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

}