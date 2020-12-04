package sk.stuba.mobv_team_7.registration

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
import sk.stuba.mobv_team_7.constants.API_KEY
import sk.stuba.mobv_team_7.constants.URL
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.databinding.RegistrationFragmentBinding
import sk.stuba.mobv_team_7.shared.SharedViewModel

class RegistrationFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var user: User

    private lateinit var binding: RegistrationFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.registration_fragment,
            container,
            false
        )

        //TODO - to send arguments to other fragments use ViewModelFactory
        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        binding.registrationViewModel = viewModel
        binding.lifecycleOwner = this

        user = User()
        binding.user = user

        viewModel.eventRegisterFinish.observe(viewLifecycleOwner, Observer { registerFinished ->
            if (registerFinished) {

                val username = user.username
                val password = user.password
                val email = user.email

                val queue = Volley.newRequestQueue(this.context)

                // JSONObject
                val jsonRootUserExists = JSONObject()
                jsonRootUserExists.put("action", "exists")
                jsonRootUserExists.put("apikey", API_KEY)
                jsonRootUserExists.put("username", username)

                if (!isFormWithErrors(email, username, password)) {
                    val jsonRequest = JsonObjectRequest(
                        URL, jsonRootUserExists,
                        Response.Listener { response ->
                            val exists: Boolean = response.get("exists") as Boolean;
                            if (!exists) {
                                register(email, username, password)
                            } else {
                                Toast.makeText(
                                    activity,
                                    "User with this username already exists.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            // TODO: crashanlytics
                            Toast.makeText(
                                activity,
                                "Unexpected error occurred.",
                                Toast.LENGTH_LONG
                            ).show()
                        })
                    queue.add(jsonRequest)
                }

                viewModel.onRegisterComplete()
            }
        })

        return binding.root
    }

    private fun registerSuccessful() {
        Toast.makeText(activity, "Registration done", Toast.LENGTH_LONG).show()
        sharedViewModel.onRegistrationSuccessful(user)
        findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment())
    }

    private fun register(email: String, username: String, password: String) {
        val jsonObject = JSONObject()
        jsonObject.put("action", "register")
        jsonObject.put("apikey", API_KEY)
        jsonObject.put("email", email)
        jsonObject.put("username", username)
        jsonObject.put("password", password)

        val queue = Volley.newRequestQueue(this.context)
        val jsonRequest = JsonObjectRequest(
            URL, jsonObject,
            Response.Listener { response ->
                user.refreshToken = response.get("refresh").toString()
                user.token = response.get("token").toString()
                registerSuccessful()
            },
            Response.ErrorListener {
                // TODO: crashanlytics
                Toast.makeText(activity, "Unexpected error occurred.", Toast.LENGTH_LONG).show()
            })
        queue.add(jsonRequest)
    }

    private fun isFormWithErrors(email: String, username: String, password: String): Boolean {
        binding.editTextUsername.error = null
        binding.editTextPassword.error = null
        binding.editTextEmail.error = null

        var errorFlag = false
        if (username.isEmpty()) {
            binding.editTextUsername.error = resources.getString(R.string.empty_error)
            errorFlag = true
        }
        if (password.isEmpty()) {
            binding.editTextPassword.error = resources.getString(R.string.empty_error)
            errorFlag = true
        }
        if (email.isEmpty()) {
            binding.editTextEmail.error = resources.getString(R.string.empty_error)
            errorFlag = true
        }
        return errorFlag

    }
}