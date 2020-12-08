package sk.stuba.mobv_team_7.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.databinding.LoginFragmentBinding
import sk.stuba.mobv_team_7.http.API_KEY
import sk.stuba.mobv_team_7.http.URL
import sk.stuba.mobv_team_7.shared.SharedViewModel

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var user: User

    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.login_fragment,
            container,
            false
        )

        //TODO - to send arguments to other fragments use ViewModelFactory
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this

        user = User()
        binding.user = user

        viewModel.eventLoginFinish.observe(viewLifecycleOwner, Observer { loginFinished ->
            if (loginFinished) {

                val queue = Volley.newRequestQueue(this.context)

                val username = user.username
                val password = user.password

                val jsonRoot = JSONObject()
                jsonRoot.put("action", "login")
                jsonRoot.put("apikey", API_KEY)
                jsonRoot.put("username", username)
                jsonRoot.put("password", password)

                if (!isFormWithErrors(username, password)) {
                    val jsonRequest = JsonObjectRequest(
                        URL, jsonRoot,
                        Response.Listener { response ->
                            user.email = response.get("email").toString()
                            user.refreshToken = response.get("refresh").toString()
                            user.token = response.get("token").toString()
                            user.profile = response.get("profile").toString()
                            loginSuccessful()
                        },
                        Response.ErrorListener {
                            // TODO: crashanlytics
                            Toast.makeText(activity, "Login not succesful.", Toast.LENGTH_LONG)
                                .show()
                        })
                    queue.add(jsonRequest)
                }

                viewModel.onLoginComplete()
            }
        })

        binding.registerButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
        }

        return binding.root
    }

    private fun loginSuccessful() {
        sharedViewModel.onLoginSuccessful(user)
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
    }

    private fun isFormWithErrors(username: String, password: String): Boolean {
        binding.editTextUsername.error = null
        binding.editTextPassword.error = null
        if (username.isNullOrEmpty() && password.isNullOrEmpty()) {
            binding.editTextUsername.error = resources.getString(R.string.empty_error)
            binding.editTextPassword.error = resources.getString(R.string.empty_error)
        } else if (username.isNullOrEmpty()) {
            binding.editTextUsername.error = resources.getString(R.string.empty_error)
        } else if (password.isNullOrEmpty()) {
            binding.editTextPassword.error = resources.getString(R.string.empty_error)
        } else {
            return false
        }
        return true
    }

}