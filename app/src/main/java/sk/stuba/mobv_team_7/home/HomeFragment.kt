package sk.stuba.mobv_team_7.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.api.PostRequest
import sk.stuba.mobv_team_7.api.UserInfoResponse
import sk.stuba.mobv_team_7.api.UserInfoRequest
import sk.stuba.mobv_team_7.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_fragment,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this


        binding.videoButton.setOnClickListener{
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToVideoFragment())
        }

//        binding.btnSearch.setOnClickListener {
//            if (binding.etProfileName.text.isNotBlank()) {
//                viewModel.getUserData(binding.etProfileName.text.toString())
//                Log.d("TAG_TAG", "click")
//            }
//        }

        binding.btnSearch.setOnClickListener {
            val postRequest = UserInfoRequest("userProfile", "wW8fG4yR7jY9bR2mY6aF2vB1jD5tE9", "da2a47dce7328a3d5b85e6cb01cf526d")

            viewModel.getUserInfo(postRequest)
            Log.d("TAG_TAG", "click")
        }

//        binding.btnSearch.setOnClickListener {
//            val postRequest = PostRequest("wW8fG4yR7jY9bR2mY6aF2vB1jD5tE9", "da2a47dce7328a3d5b85e6cb01cf526d")
//
//            viewModel.postNewPost(postRequest)
//            Log.d("TAG_TAG", "click")
//        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.navbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        return true
    }

}