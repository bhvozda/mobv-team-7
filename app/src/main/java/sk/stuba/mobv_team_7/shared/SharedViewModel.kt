package sk.stuba.mobv_team_7.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.home.PostDto
import java.io.File

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

    private val _eventPostUpload = MutableLiveData<File>()
    val eventPostUpload: LiveData<File>
        get() = _eventPostUpload

    private val _eventUploadFlag = MutableLiveData<Boolean>()
    val eventUploadFlag: LiveData<Boolean>
        get() = _eventUploadFlag

    fun onLoginSuccessful(user: User) {
        _eventLoginSuccessful.value = user
    }

    fun onRegistrationSuccessful(user: User) {
        _eventRegistrationSuccessful.value = user
    }

    fun onPostChoice(post: PostDto) {
        _eventPostChoice.value = post
    }

    fun onPostUpload(post: File, flag: Boolean) {
        _eventPostUpload.postValue(post)
        _eventUploadFlag.postValue(flag)
    }

    fun clearPostFlag() {
        _eventUploadFlag.postValue(false)
    }
}