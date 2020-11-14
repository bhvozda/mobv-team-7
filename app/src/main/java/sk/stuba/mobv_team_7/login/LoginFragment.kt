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
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

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

        viewModel.eventLoginFinish.observe(viewLifecycleOwner, Observer { loginFinished ->
            if (loginFinished){
                loginSuccesful()
                viewModel.onLoginComplete()
            }
        })

        viewModel.eventRegisterFinish.observe(viewLifecycleOwner, Observer { registerFinished ->
            if (registerFinished){
                registerSuccesful()
                viewModel.onRegisterComplete()
            }
        })

        return binding.root
    }

    private fun loginSuccesful() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
    }

    private fun registerSuccesful() {
        //TODO do something after register
        Toast.makeText(activity, "Registration done", Toast.LENGTH_LONG).show()
    }
}