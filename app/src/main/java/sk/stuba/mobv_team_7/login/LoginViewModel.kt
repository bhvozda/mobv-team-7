package sk.stuba.mobv_team_7.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    // The login finished event
    private val _eventLoginFinish = MutableLiveData<Boolean>()
    val eventLoginFinish: LiveData<Boolean>
        get() = _eventLoginFinish

    // The register finished event
    private val _eventRegisterFinish = MutableLiveData<Boolean>()
    val eventRegisterFinish: LiveData<Boolean>
        get() = _eventRegisterFinish

    /** Methods for buttons presses **/
    fun onLogin() {
        //TODO Do the login
        _eventLoginFinish.value = true
    }

    fun onRegister() {
        //TODO Do the registration
        _eventRegisterFinish.value = true
    }

    fun onLoginComplete() {
        _eventLoginFinish.value = false
    }

    fun onRegisterComplete() {
        _eventRegisterFinish.value = false
    }
}