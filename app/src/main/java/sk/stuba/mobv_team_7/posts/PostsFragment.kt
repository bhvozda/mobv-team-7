package sk.stuba.mobv_team_7.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.databinding.PostsFragmentBinding
import sk.stuba.mobv_team_7.shared.SharedViewModel

class PostsFragment : Fragment() {
    private lateinit var viewModel: PostsViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var user: User

    private lateinit var binding: PostsFragmentBinding

    override fun onStart() {
        println("XXXXXXXXXXXX")
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.posts_fragment,
            container,
            false
        )

        //TODO - to send arguments to other fragments use ViewModelFactory
        viewModel = ViewModelProvider(this).get(PostsViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        binding.postsViewModel = viewModel
        binding.lifecycleOwner = this

        user = User()
//        binding.user = user

//        ### Prispevky
//        POST http://api.mcomputing.eu/mobv/service.php
//        Accept: application/json
//        Cache-Control: no-cache
//        Content-Type: application/json
//
//        {
//            "action": "posts",
//            "apikey": "bX8eF8uH3oV3aK9iB5xG0lX6eY5rT4",
//            "token": "23dc378945cecf28097ef131045f305e"
//        }


        return binding.root
    }

}