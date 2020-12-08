package sk.stuba.mobv_team_7.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.camera2.*
import android.media.MediaCodec
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.video_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.databinding.VideoFragmentBinding
import sk.stuba.mobv_team_7.shared.SharedViewModel
import sk.stuba.mobv_team_7.utils.AutoFitSurfaceView
import sk.stuba.mobv_team_7.utils.OrientationLiveData
import sk.stuba.mobv_team_7.utils.getPreviewOutputSize
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log

class VideoFragment : Fragment() {

    /**
     * id: 0 - back camera
     * id: 1 - front camera
     * */
    private var cameraId : String = "0"
    private var width = 1920
    private var height = 1080
    private val fps = 30

    private lateinit var viewModel: VideoViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var binding: VideoFragmentBinding

    private lateinit var token: String
    private lateinit var cameraList: List<CameraInfo>

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** [CameraCharacteristics] corresponding to the provided Camera ID */
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(cameraId)
    }

    /** File where the recording will be saved */
    private val outputFile: File by lazy { createFile(requireContext(), "mp4") }

    /**
     * Setup a persistent [Surface] for the recorder so we can use it as an output target for the
     * camera session without preparing the recorder
     */
    private val recorderSurface: Surface by lazy {

        // Get a persistent Surface from MediaCodec, don't forget to release when done
        val surface = MediaCodec.createPersistentInputSurface()

        // Prepare and release a dummy MediaRecorder with our new surface
        // Required to allocate an appropriately sized buffer before passing the Surface as the
        //  output target to the capture session
        createRecorder(surface).apply {
            prepare()
            release()
        }

        surface
    }

    /** Saves the video recording */
    private val recorder: MediaRecorder by lazy { createRecorder(recorderSurface) }

    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)

    /** Performs recording animation of flashing screen */
    private val animationTask: Runnable by lazy {
        Runnable {
            // Flash white animation
            overlay.foreground = Color.argb(150, 255, 255, 255).toDrawable()
            // Wait for ANIMATION_FAST_MILLIS
            overlay.postDelayed({
                // Remove white flash animation
                overlay.foreground = null
                // Restart animation recursively
                overlay.postDelayed(animationTask, ANIMATION_FAST_MILLIS)
            }, ANIMATION_FAST_MILLIS)
        }
    }

    /** Where the camera preview is displayed */
    private lateinit var viewFinder: AutoFitSurfaceView

    /** Overlay on top of the camera preview */
    private lateinit var overlay: View

    /** Captures frames from a [CameraDevice] for our video recording */
    private lateinit var session: CameraCaptureSession

    /** The [CameraDevice] that will be opened in this fragment */
    private lateinit var camera: CameraDevice

    /** Requests used for preview only in the [CameraCaptureSession] */
    private val previewRequest: CaptureRequest by lazy {
        // Capture request holds references to target surfaces
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
            // Add the preview surface target
            addTarget(viewFinder.holder.surface)
        }.build()
    }

    /** Requests used for preview and recording in the [CameraCaptureSession] */
    private val recordRequest: CaptureRequest by lazy {
        // Capture request holds references to target surfaces
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            // Add the preview and recording surface targets
            addTarget(viewFinder.holder.surface)
            addTarget(recorderSurface)
            // Sets user requested FPS for all targets
            set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(fps, fps))
        }.build()
    }

    private var recordingStartMillis: Long = 0L

    /** Live data listener for changes in the device orientation relative to the camera */
    private lateinit var relativeOrientation: OrientationLiveData

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

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.eventLoginSuccessful.observe(viewLifecycleOwner, Observer { user ->
            token = user.token.toString()
        })

        cameraList = enumerateVideoCameras(cameraManager)
        if (cameraList.isNotEmpty()) {
            cameraId = cameraList[0].cameraId
        } else {
            Toast.makeText(context, "We are sorry, but your phone does not support fullHD recording", Toast.LENGTH_SHORT).show()
            sharedViewModel.setForbidVideoFlag()
            findNavController().navigate(VideoFragmentDirections.actionVideoFragmentPopToHomeFragment())
        }

        binding.flipButton.setOnClickListener{
            cameraId = if (cameraId == "0") {
                "1"
            } else {
                "0"
            }
        }

        binding.videoViewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        overlay = view.findViewById(R.id.overlay)
        viewFinder = view.findViewById(R.id.view_finder)

        viewFinder.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(holder: SurfaceHolder) = Unit
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int) = Unit

            override fun surfaceCreated(holder: SurfaceHolder) {

                // Selects appropriate preview size and configures view finder
                val previewSize = getPreviewOutputSize(
                    viewFinder.display, characteristics, SurfaceHolder::class.java)
                Log.d(TAG, "View finder size: ${viewFinder.width} x ${viewFinder.height}")
                Log.d(TAG, "Selected preview size: $previewSize")
                viewFinder.setAspectRatio(previewSize.width, previewSize.height)

                // To ensure that size is set, initialize camera in the view's thread
                viewFinder.post { initializeCamera() }
            }
        })

        // Used to rotate the output media to match device orientation
        relativeOrientation = OrientationLiveData(requireContext(), characteristics).apply {
            observe(viewLifecycleOwner, Observer {
                    orientation -> Log.d(TAG, "Orientation changed: $orientation")
            })
        }

        if (cameraList.size > 1) {
            binding.flipButton.visibility = View.VISIBLE
        }

        val cameraManager =
            requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** Lists all video-capable cameras and supported resolution and FPS combinations */
    @SuppressLint("InlinedApi")
    private fun enumerateVideoCameras(cameraManager: CameraManager): List<CameraInfo> {
        val availableCameras: MutableList<CameraInfo> = mutableListOf()

        // Iterate over the list of cameras and add those with high speed video recording
        //  capability to our output. This function only returns those cameras that declare
        //  constrained high speed video recording, but some cameras may be capable of doing
        //  unconstrained video recording with high enough FPS for some use cases and they will
        //  not necessarily declare constrained high speed video capability.
        cameraManager.cameraIdList.forEach { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val orientation = lensOrientationString(
                characteristics.get(CameraCharacteristics.LENS_FACING)!!)

            // Query the available capabilities and output formats
            val capabilities = characteristics.get(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)!!
            val cameraConfig = characteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!

            // Return cameras that declare to be backward compatible
            if (capabilities.contains(CameraCharacteristics
                    .REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE)) {
                // Recording should always be done in the most efficient format, which is
                //  the format native to the camera framework
                val targetClass = MediaRecorder::class.java

                // For each size, list the expected FPS
                cameraConfig.getOutputSizes(targetClass).forEach { size ->
                    // Get the number of seconds that each frame will take to process
                    if (1920 == size.width && 1080 == size.height) {
                        val secondsPerFrame =
                            cameraConfig.getOutputMinFrameDuration(targetClass, size) /
                                    1_000_000_000.0
                        // Compute the frames per second to let user select a configuration
                        val fps = if (secondsPerFrame > 0) (1.0 / secondsPerFrame).toInt() else 0
                        val fpsLabel = if (fps > 0) "$fps" else "N/A"
                        availableCameras.add(CameraInfo(
                            "$orientation ($id) $size $fpsLabel FPS", id, size, fps))
                    }
                }
            }
        }

        return availableCameras
    }

    /** Creates a [MediaRecorder] instance using the provided [Surface] as input */
    private fun createRecorder(surface: Surface) = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setOutputFile(outputFile.absolutePath)
        setVideoEncodingBitRate(RECORDER_VIDEO_BITRATE)
        setVideoFrameRate(fps)
        Log.d(TAG, "Selected preview size: $characteristics")
        setVideoSize(width, height)
        // 8MB - 1024 * 1024 * 8
        setMaxFileSize(8388608L)
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setInputSurface(surface)
    }

    /**
     * Begin all camera operations in a coroutine in the main thread. This function:
     * - Opens the camera
     * - Configures the camera session
     * - Starts the preview by dispatching a repeating request
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initializeCamera() = lifecycleScope.launch(Dispatchers.Main) {

        // Open the selected camera
        camera = openCamera(cameraManager, cameraId, cameraHandler)

        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(viewFinder.holder.surface, recorderSurface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)

        // Sends the capture request as frequently as possible until the session is torn down or
        //  session.stopRepeating() is called
        session.setRepeatingRequest(previewRequest, null, cameraHandler)


        /** Start recording event */
        capture_button.setOnClickListener {

            capture_button.visibility = View.GONE
            capture_off_button.visibility = View.VISIBLE

            // Prevents screen rotation during the video recording
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
            // Start recording repeating requests, which will stop the ongoing preview
            //  repeating requests without having to explicitly call `session.stopRepeating`
            session.setRepeatingRequest(recordRequest, null, cameraHandler)
            // Finalizes recorder setup and starts recording
            recorder.apply {
                // Sets output orientation based on current sensor value at start time
                relativeOrientation.value?.let { setOrientationHint(it) }
                prepare()
                start()
            }
            recordingStartMillis = System.currentTimeMillis()
            Log.d(TAG, "Recording started")
            // Starts recording animation
            overlay.post(animationTask)
        }

        /** Finish recording event */
        capture_off_button.setOnClickListener {

            capture_button.visibility = View.VISIBLE
            capture_off_button.visibility = View.GONE

            // Unlocks screen rotation after recording finished
            requireActivity().requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            Log.d(TAG, "Recording stopped. Output file: $outputFile")
            recorder.stop()

            // Removes recording animation
            overlay.removeCallbacks(animationTask)
            // Broadcasts the media file to the rest of the system
            MediaScannerConnection.scanFile(view?.context, arrayOf(outputFile.absolutePath), null, null)

            sendVideoOnReview(outputFile)
        }
    }

    // Go to preview fragment and see video
    private fun sendVideoOnReview(post: File) {
        sharedViewModel.onPostUpload(post, true)
        findNavController().navigate(VideoFragmentDirections.actionVideoFragmentToVideoPlayerFragment())
    }

    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) = cont.resume(device)

            override fun onDisconnected(device: CameraDevice) {
                Log.w(TAG, "Camera $cameraId has been disconnected")
                requireActivity().finish()
            }

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when(error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    /**
     * Creates a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine)
     */
    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->

        // Creates a capture session using the predefined targets, and defines a session state
        // callback which resumes the coroutine once the session is configured
        device.createCaptureSession(targets, object: CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }

    override fun onStop() {
        super.onStop()
        try {
            camera.close()
        } catch (exc: Throwable) {
            Log.e(TAG, "Error closing camera", exc)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraThread.quitSafely()
        recorder.release()
        recorderSurface.release()
    }

    companion object {

        private data class CameraInfo(
            val name: String,
            val cameraId: String,
            val size: Size,
            val fps: Int)

        /** Converts a lens orientation enum into a human-readable string */
        private fun lensOrientationString(value: Int) = when (value) {
            CameraCharacteristics.LENS_FACING_BACK -> "Back"
            CameraCharacteristics.LENS_FACING_FRONT -> "Front"
            CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
            else -> "Unknown"
        }

        private val TAG = VideoFragment::class.java.simpleName

        /** Milliseconds used for UI animations */
        const val ANIMATION_FAST_MILLIS = 200L
        const val ANIMATION_SLOW_MILLIS = 500L
        private const val IMMERSIVE_FLAG_TIMEOUT = 500L

        private const val RECORDER_VIDEO_BITRATE: Int = 10_000_000
        private const val MIN_REQUIRED_RECORDING_TIME_MILLIS: Long = 1000L

        /** Creates a [File] named with the current date and time */
        private fun createFile(context: Context, extension: String): File {
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US)
            return File(context.filesDir, "VID_${sdf.format(Date())}.$extension")
        }
    }

}