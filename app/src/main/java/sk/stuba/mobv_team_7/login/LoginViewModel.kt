package sk.stuba.mobv_team_7.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel() : ViewModel() {

    // The login finished event
    private val _eventLoginFinish = MutableLiveData<Boolean>()
    val eventLoginFinish: LiveData<Boolean>
        get() = _eventLoginFinish

    /** Methods for buttons presses **/
    fun onLogin() {
        _eventLoginFinish.value = true
    }

    fun onLoginComplete() {
        _eventLoginFinish.value = false
    }

}