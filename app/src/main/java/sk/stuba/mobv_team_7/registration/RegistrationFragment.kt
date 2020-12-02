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

                val jsonRoot = JSONObject()
                jsonRoot.put("action", "register")
                jsonRoot.put("apikey", API_KEY)
                jsonRoot.put("email", email)
                jsonRoot.put("username", username)
                jsonRoot.put("password", password)

                println(jsonRoot.toString())

                val jsonRequest = JsonObjectRequest(
                    URL, jsonRoot,
                    Response.Listener { response ->
                        registerSuccessful()
                        println(response)
                    },
                    Response.ErrorListener {
                        println("error")
                    })
                queue.add(jsonRequest)

                viewModel.onRegisterComplete()
            }
        })

        return binding.root
    }

    private fun registrationSuccessful() {
        findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToHomeFragment())
    }

    private fun registerSuccessful() {
        //TODO do something after register
        Toast.makeText(activity, "Registration done", Toast.LENGTH_LONG).show()
    }
}