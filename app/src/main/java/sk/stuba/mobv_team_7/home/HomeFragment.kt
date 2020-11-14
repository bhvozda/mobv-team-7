package sk.stuba.mobv_team_7.home

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.databinding.HomeFragmentBinding
import sk.stuba.mobv_team_7.databinding.LoginFragmentBinding

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<HomeFragmentBinding>(
            inflater, R.layout.home_fragment, container, false)

        binding.home = this

        //The complete onClickListener with Navigation using createNavigateOnClickListener
        binding.profileButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_profileFragment)
        )

        binding.videoButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_videoFragment)
        )

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}