package sk.stuba.mobv_team_7.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationViewModel() : ViewModel() {

    // The register finished event
    private val _eventRegisterFinish = MutableLiveData<Boolean>()
    val eventRegisterFinish: LiveData<Boolean>
        get() = _eventRegisterFinish

    private val _eventGoToLogin = MutableLiveData<Boolean>()
    val eventGoToLogin: LiveData<Boolean>
        get() = _eventGoToLogin

    /** Methods for buttons presses **/
    fun onRegister() {
        _eventRegisterFinish.value = true
    }

    fun onRegisterComplete() {
        _eventRegisterFinish.value = false
    }

    fun onOnLoginClick() {
        _eventGoToLogin.value = true
    }

}