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

class RegistrationFragment : Fragment() {

    private lateinit var viewModel: RegistrationViewModel
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

                if (!isFormWithErrors(email, username, password)){
                    val jsonRequest = JsonObjectRequest(
                        URL, jsonRootUserExists,
                        Response.Listener { response ->
                            val exists: Boolean = response.get("exists") as Boolean;
                            if (!exists) {
                                register(email, username, password)
                            } else {
                                Toast.makeText(activity, "User with this username already exists.", Toast.LENGTH_LONG).show()
                            }
                        },
                        Response.ErrorListener {
                            // TODO: crashanlytics
                            Toast.makeText(activity, "Unexpected error occurred.", Toast.LENGTH_LONG).show()
                        })
                    queue.add(jsonRequest)
                }

                viewModel.onRegisterComplete()
            }
        })

        return binding.root
    }

    private fun registerSuccessful() {
        //TODO do something after register
        Toast.makeText(activity, "Registration done", Toast.LENGTH_LONG).show()
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
                registerSuccessful()
            },
            Response.ErrorListener {
                // TODO: crashanlytics
                Toast.makeText(activity, "Unexpected error occurred.", Toast.LENGTH_LONG).show()
            })
        queue.add(jsonRequest)
    }

    private fun isFormWithErrors(email: String, username: String, password: String): Boolean{
        binding.editTextUsername.error = null
        binding.editTextPassword.error = null
        binding.editTextEmail.error = null
        if (username.isNullOrEmpty() && password.isNullOrEmpty() && email.isNullOrEmpty()){
            binding.editTextUsername.error = resources.getString(R.string.empty_error)
            binding.editTextPassword.error = resources.getString(R.string.empty_error)
            binding.editTextEmail.error = resources.getString(R.string.empty_error)
        }else if(username.isNullOrEmpty() && password.isNullOrEmpty()){
            binding.editTextUsername.error = resources.getString(R.string.empty_error)
            binding.editTextPassword.error = resources.getString(R.string.empty_error)
        }else if(username.isNullOrEmpty() && email.isNullOrEmpty()){
            binding.editTextUsername.error = resources.getString(R.string.empty_error)
            binding.editTextEmail.error = resources.getString(R.string.empty_error)
        }else if(password.isNullOrEmpty() && email.isNullOrEmpty()){
            binding.editTextPassword.error = resources.getString(R.string.empty_error)
            binding.editTextEmail.error = resources.getString(R.string.empty_error)
        }else if(username.isNullOrEmpty()){
            binding.editTextEmail.error = resources.getString(R.string.empty_error)
        }else if(password.isNullOrEmpty()){
            binding.editTextPassword.error = resources.getString(R.string.empty_error)
        }else if(email.isNullOrEmpty()){
            binding.editTextEmail.error = resources.getString(R.string.empty_error)
        }else{
            return false
        }
        return true
    }
}