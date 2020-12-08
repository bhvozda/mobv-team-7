package sk.stuba.mobv_team_7.password

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.databinding.PasswordFragmentBinding
import sk.stuba.mobv_team_7.http.API_KEY
import sk.stuba.mobv_team_7.http.URL
import sk.stuba.mobv_team_7.shared.SharedViewModel

class PasswordFragment : Fragment() {

    private lateinit var viewModel: PasswordViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: PasswordFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.password_fragment,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)
        binding.buttonChange.setOnClickListener{
            sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
            sharedViewModel.eventLoginSuccessful.observe(viewLifecycleOwner, Observer { user ->
                // TODO: password logic
                if (!isCorrectForm(user.password)){
                    val jsonObject = JSONObject()
                    jsonObject.put("action", "password")
                    jsonObject.put("apikey", API_KEY)
                    jsonObject.put("token", user.token)
                    jsonObject.put("oldpassword", user.password)
                    jsonObject.put("newpassword", binding.editTextNewPassword.text.toString())

                    val queue = Volley.newRequestQueue(this.context)
                    val jsonRequest = JsonObjectRequest(
                        URL, jsonObject,
                        Response.Listener { response ->
                            user.email = response.get("email").toString()
                            user.refreshToken = response.get("refresh").toString()
                            user.token = response.get("token").toString()
                            user.profile = response.get("profile").toString()
                            changeSuccessful()
                        },
                        Response.ErrorListener {
                            // TODO: crashanlytics
                            Toast.makeText(activity, "Unexpected error occurred.", Toast.LENGTH_LONG).show()
                        })
                    queue.add(jsonRequest)
                }

            })

        }
        return binding.root

    }
    private fun isCorrectForm(password: String):Boolean {
        binding.editTextOldPass.error = null
        binding.editTextNewPassword.error = null

        if(!binding.editTextOldPass.text.toString().equals(password) && !binding.editTextOldPass.text.isNullOrEmpty()){
            binding.editTextOldPass.error = "Incorrect password"
            if(binding.editTextNewPassword.text.isNullOrEmpty()){
                binding.editTextNewPassword.error = resources.getString(R.string.empty_error)
            }
        }
        else if(binding.editTextOldPass.text.isNullOrEmpty() && binding.editTextNewPassword.text.isNullOrEmpty()){
            binding.editTextOldPass.error = resources.getString(R.string.empty_error)
            binding.editTextNewPassword.error = resources.getString(R.string.empty_error) }
        else if (binding.editTextOldPass.text.isNullOrEmpty()) {
            binding.editTextOldPass.error = resources.getString(R.string.empty_error) }
        else if (binding.editTextNewPassword.text.isNullOrEmpty()) {
            binding.editTextNewPassword.error = resources.getString(R.string.empty_error) }
        else {
            return false
        }
        return true
    }

    private fun changeSuccessful() {
        Toast.makeText(activity, "Your password has been changed successfully! ", Toast.LENGTH_LONG).show()
        findNavController().navigate(PasswordFragmentDirections.actionPasswordFragmentToProfileFragment())
    }

}