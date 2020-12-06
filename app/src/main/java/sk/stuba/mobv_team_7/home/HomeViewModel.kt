package sk.stuba.mobv_team_7.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

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