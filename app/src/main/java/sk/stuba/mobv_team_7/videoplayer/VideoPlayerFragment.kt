package sk.stuba.mobv_team_7.videoplayer

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
import sk.stuba.mobv_team_7.databinding.VideoPlayerFragmentBinding
import sk.stuba.mobv_team_7.login.LoginFragmentDirections
import sk.stuba.mobv_team_7.video.VideoFragmentDirections

class VideoPlayerFragment: Fragment() {

    private lateinit var viewModel: VideoPlayerViewModel

    private lateinit var binding: VideoPlayerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.video_player_fragment,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(VideoPlayerViewModel::class.java)

        binding.videoPlayerViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.eventOpenPost.observe(viewLifecycleOwner, Observer { postOpened ->
            if (postOpened){
                postOpenedSuccessfully()
                viewModel.onPostOpenComplete()
            }
        })

        return binding.root
    }

    private fun postOpenedSuccessfully() {
        findNavController().navigate(VideoPlayerFragmentDirections.actionVideoPlayerFragmentToHomeFragment())
    }
}