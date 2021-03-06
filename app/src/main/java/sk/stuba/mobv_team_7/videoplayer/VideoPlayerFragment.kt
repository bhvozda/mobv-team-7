package sk.stuba.mobv_team_7.videoplayer

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.api.PostRequest
import sk.stuba.mobv_team_7.databinding.VideoPlayerFragmentBinding
import sk.stuba.mobv_team_7.http.API_KEY
import sk.stuba.mobv_team_7.http.VIEW_POST_URL
import sk.stuba.mobv_team_7.shared.SharedViewModel
import java.io.File

class VideoPlayerFragment: Fragment() {

    private lateinit var viewModel: VideoPlayerViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var binding: VideoPlayerFragmentBinding

    private lateinit var token: String
    private lateinit var outputFile: File

    private var exoplayerView : PlayerView? = null
    private var exoplayer : SimpleExoPlayer? = null
    private var playbackStateBuilder : PlaybackStateCompat.Builder? = null
    private var mediaSession: MediaSessionCompat? = null

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
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        binding.videoPlayerViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.eventOpenPost.observe(viewLifecycleOwner, Observer { postOpened ->
            if (postOpened){
                postOpenedSuccessfully()
                viewModel.onPostOpenComplete()
            }
        })

        exoplayerView = binding.videoPlayerPlayerView

        /** Check if upload flag is on
         *
         * if going from Video fragment we let user preview recorded video and upload it
         * if going from home fragment we will display video from posts
         * */
        sharedViewModel.eventUploadFlag.observe(viewLifecycleOwner, Observer { flag ->
            if (flag == true) {
                /** GET Token for video upload  */
                sharedViewModel.eventLoginSuccessful.observe(viewLifecycleOwner, Observer { user ->
                    token = user.token.toString()
                })
                binding.videoPlayerButton.visibility = View.VISIBLE
                /** SHOW Captured video preview */
                sharedViewModel.eventPostUpload.observe(viewLifecycleOwner, Observer { post ->
                    outputFile = post
                    initializePlayer(null, Uri.fromFile(post))
                })
            } else {
                /** SHOW Video from internet  */
                sharedViewModel.eventPostChoice.observe(viewLifecycleOwner, Observer { post ->
                    initializePlayer(VIEW_POST_URL + post.videourl, null)
                })
            }
        })

        return binding.root
    }

    private fun initializePlayer(url: String?, uri: Uri?) {
        val trackSelector = DefaultTrackSelector()
        exoplayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        exoplayerView?.player = exoplayer

        val userAgent = Util.getUserAgent(context, "Exo")

        val mediaUri: Uri
        if (uri != null) {
            mediaUri = uri
        } else {
            mediaUri = Uri.parse(url)
        }

        val mediaSource = ExtractorMediaSource(mediaUri, DefaultDataSourceFactory(context, userAgent), DefaultExtractorsFactory(), null, null)

        exoplayer?.prepare(mediaSource)

        val componentName = context?.let { ComponentName(it, "Exo") }
        mediaSession = MediaSessionCompat(context, "ExoPlayer", componentName, null)

        playbackStateBuilder = PlaybackStateCompat.Builder()

        playbackStateBuilder?.setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_FAST_FORWARD)

        mediaSession?.setPlaybackState(playbackStateBuilder?.build())
        mediaSession?.isActive = true
    }

    // Go fullscreen
    @Override
    override fun onResume()
    {
        super.onResume()
        (context as AppCompatActivity).supportActionBar?.hide()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    // Show toolbar again
    @Override
    override fun onPause()
    {
        super.onPause()
        (context as AppCompatActivity).supportActionBar?.show()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        if (exoplayer != null) {
            exoplayer?.stop()
            exoplayer?.release()
            exoplayer = null
        }
    }

    private fun postOpenedSuccessfully() {
        val postRequest = PostRequest(API_KEY, token)

        viewModel.postNewPost(postRequest, outputFile)
        Log.d("TAG_TAG", "file saving")

        sharedViewModel.clearPostFlag()

        findNavController().navigate(VideoPlayerFragmentDirections.actionVideoPlayerFragmentToHomeFragment())
    }
}