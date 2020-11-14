package sk.stuba.mobv_team_7.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.databinding.ProfileFragmentBinding

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_fragment,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        binding.profileViewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

}