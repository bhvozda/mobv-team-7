package sk.stuba.mobv_team_7.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.home.PostDto

class SharedViewModel(): ViewModel() {

    private val _eventLoginSuccessful = MutableLiveData<User>()
    val eventLoginSuccessful: LiveData<User>
        get() = _eventLoginSuccessful

    private val _eventRegistrationSuccessful = MutableLiveData<User>()
    val eventRegistrationSuccessful: LiveData<User>
        get() = _eventRegistrationSuccessful

    private val _eventPostChoice = MutableLiveData<PostDto>()
    val eventPostChoice: LiveData<PostDto>
        get() = _eventPostChoice

    fun onLoginSuccessful(user: User) {
        _eventLoginSuccessful.value = user
    }

    fun onRegistrationSuccessful(user: User) {
        _eventRegistrationSuccessful.value = user
    }

    fun onPostChoice(post: PostDto) {
        _eventPostChoice.value = post
    }
}