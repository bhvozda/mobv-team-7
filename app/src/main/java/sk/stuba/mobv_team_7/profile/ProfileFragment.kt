package sk.stuba.mobv_team_7.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.databinding.ProfileFragmentBinding
import sk.stuba.mobv_team_7.shared.SharedViewModel

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Fresco.initialize(activity)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_fragment,
            container,
            false
        )

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.eventLoginSuccessful.observe(viewLifecycleOwner, Observer { user ->
            // TODO: profile logic
            binding.name.text = user.username
            binding.sendEmail.text = user.email
        })

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = this


        //task-10
        val path = "res:/" + R.drawable.default_avatar;
        binding.profilePicture.setImageURI(path)


        binding.logOut.setOnClickListener{
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
        }

        binding.changePassword.setOnClickListener{
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToPasswordFragment())
        }

        return binding.root
    }

}