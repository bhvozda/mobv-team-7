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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    private final val API_KEY = "wW8fG4yR7jY9bR2mY6aF2vB1jD5tE9"
    private lateinit var viewModel: LoginViewModel
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

        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this

        user = User()
        binding.user = user

        viewModel.eventLoginFinish.observe(viewLifecycleOwner, Observer { loginFinished ->
            if (loginFinished) {

                val queue = Volley.newRequestQueue(this.context)
                val url = "http://api.mcomputing.eu/mobv/service.php"

                val username = user.username
                val password = user.password

                val jsonRoot = JSONObject()
                jsonRoot.put("action", "login")
                jsonRoot.put("apikey", API_KEY)
                jsonRoot.put("username", username)
                jsonRoot.put("password", password)

                val jsonRequest = JsonObjectRequest(url, jsonRoot,
                    Response.Listener { response ->
                        loginSuccessful()
                        println(response)
                    },
                    Response.ErrorListener {
                        println(it)
                    })
                queue.add(jsonRequest)

                viewModel.onLoginComplete()
            }
        })

        viewModel.eventRegisterFinish.observe(viewLifecycleOwner, Observer { registerFinished ->
            if (registerFinished) {

                val username = user.username
                val password = user.password

                val queue = Volley.newRequestQueue(this.context)
                val url = "http://api.mcomputing.eu/mobv/service.php"

                val jsonRoot = JSONObject()
                jsonRoot.put("action", "register")
                jsonRoot.put("apikey", API_KEY)
                jsonRoot.put("email", "user@user.com")
                jsonRoot.put("username", username)
                jsonRoot.put("password", password)

                println(jsonRoot.toString())

                val jsonRequest = JsonObjectRequest(url, jsonRoot,
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

    private fun loginSuccessful() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
    }

    private fun registerSuccessful() {
        //TODO do something after register
        Toast.makeText(activity, "Registration done", Toast.LENGTH_LONG).show()
    }
}