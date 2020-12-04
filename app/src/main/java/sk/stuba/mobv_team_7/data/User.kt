package sk.stuba.mobv_team_7.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import sk.stuba.mobv_team_7.BR

class User : BaseObservable() {

    @Bindable
    var username: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.username)
        }
        get() = field

    @Bindable
    var password: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }
        get() = field

    @Bindable
    var email: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }
        get() = field

    var refreshToken: String? = null
    var token: String? = null

}