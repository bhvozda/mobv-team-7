package sk.stuba.mobv_team_7.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.databinding.VideoFragmentBinding

class VideoFragment : Fragment() {

    private lateinit var viewModel: VideoViewModel

    private lateinit var binding: VideoFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.video_fragment,
            container,
            false
        )

        viewModel = ViewModelProvider(this).get(VideoViewModel::class.java)

        binding.videoViewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }


}