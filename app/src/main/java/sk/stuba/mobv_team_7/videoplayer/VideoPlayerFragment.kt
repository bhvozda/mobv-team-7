package sk.stuba.mobv_team_7.videoplayer

import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.custom_controller.view.*
import kotlinx.android.synthetic.main.video_player_fragment.*
import sk.stuba.mobv_team_7.MainActivity
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.databinding.VideoPlayerFragmentBinding

class VideoPlayerFragment: Fragment() {

    private lateinit var viewModel: VideoPlayerViewModel

    private lateinit var binding: VideoPlayerFragmentBinding

    private var exoplayerView : SimpleExoPlayerView? = null
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

        binding.videoPlayerViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.eventOpenPost.observe(viewLifecycleOwner, Observer { postOpened ->
            if (postOpened){
                postOpenedSuccessfully()
                viewModel.onPostOpenComplete()
            }
        })

        exoplayerView = binding.simpleExoPlayerView
        val videoUrl = "https://i.imgur.com/7bMqysJ.mp4"
        initializePlayer(videoUrl)

        return binding.root
    }

    private fun initializePlayer(url: String) {
        val trackSelector = DefaultTrackSelector()
        exoplayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        exoplayerView?.player = exoplayer

        val userAgent = Util.getUserAgent(context, "Exo")
        val mediaUri = Uri.parse(url)
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
        findNavController().navigate(VideoPlayerFragmentDirections.actionVideoPlayerFragmentToHomeFragment())
    }
}