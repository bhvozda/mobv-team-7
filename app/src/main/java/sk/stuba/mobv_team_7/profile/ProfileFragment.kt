package sk.stuba.mobv_team_7.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipeline
import kotlinx.android.synthetic.main.profile_picture_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.api.PhotoUpdateRequest
import sk.stuba.mobv_team_7.databinding.ProfileFragmentBinding
import sk.stuba.mobv_team_7.http.API_KEY
import sk.stuba.mobv_team_7.http.URL
import sk.stuba.mobv_team_7.shared.SharedViewModel
import sk.stuba.mobv_team_7.utils.URIPathHelper
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var binding: ProfileFragmentBinding

    private lateinit var token: String

    private lateinit var newProfilePicture: File

    private var mConstraintLayout: ConstraintLayout? = null
    private val mConstraintSet: ConstraintSet = ConstraintSet()
    private var menuList: Menu? = null

    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

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
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.sharedViewModel = sharedViewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        mConstraintLayout = binding.constraintLayout

        sharedViewModel.eventLoginSuccessful.observe(viewLifecycleOwner, Observer {
            val profile = sharedViewModel.user.value?.profile
            token = sharedViewModel.user.value?.token.toString()

            if (!profile.isNullOrEmpty()) {
                val uri: Uri =
                    Uri.parse("http://api.mcomputing.eu/mobv/uploads/$profile")
                lifecycleScope.launch(Dispatchers.IO) {
                    binding.profilePicture.setImageURI(uri)
                    Fresco.getImagePipeline().evictFromCache(uri);
                    val imagePipeline: ImagePipeline = Fresco.getImagePipeline()
                    imagePipeline.clearMemoryCaches()
                    imagePipeline.clearDiskCaches()
                }
            }
        })

        viewModel.eventGalleryPictureUpload.observe(viewLifecycleOwner, Observer { succesful ->
                if (succesful) {
                    val item = menuList!!.findItem(R.id.saveProfile)
                    item.isVisible = true
                    viewModel.onImageUploadComplete()
                }
            })

        viewModel.postStatus.observe(viewLifecycleOwner, Observer { status ->
            if (status != null) {

                val item = menuList!!.findItem(R.id.saveProfile)
                item.isVisible = false

                binding.profilePicture.visibility = View.VISIBLE
                binding.profilePictureAvatar.visibility = View.GONE

                mConstraintSet.clone(mConstraintLayout)
                mConstraintSet.connect(binding.name.id, ConstraintSet.TOP,
                    binding.profilePicture.id, ConstraintSet.BOTTOM)
                mConstraintSet.applyTo(mConstraintLayout)

                // Update shared view model after new image was uploaded
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.getUpdatedUserData(token)
                }

                viewModel.onImagePostComplete()
            }
        })

        viewModel.eventUserUpdated.observe(viewLifecycleOwner, Observer { userProfile ->
            if (!userProfile.isNullOrEmpty()){
                sharedViewModel.user.value?.profile = userProfile
                viewModel.onGetUpdatedUserDataComplete()
            }
        })

        val path = "res:/" + R.drawable.default_avatar;
        binding.profilePicture.setImageURI(path)
        binding.profilePictureAvatar.setImageURI(path)

        binding.profilePicture.setOnClickListener {
            showMessageBox()
        }

        binding.profilePictureAvatar.setOnClickListener {
            showMessageBox()
        }


        binding.logOut.setOnClickListener {
            val queue = Volley.newRequestQueue(this.context)

            val jsonRoot = JSONObject()
            jsonRoot.put("action", "refreshToken")
            jsonRoot.put("apikey", API_KEY)
            jsonRoot.put("refreshToken", sharedViewModel.user.value?.refreshToken)

            val jsonRequest = JsonObjectRequest(
                URL, jsonRoot,
                Response.Listener { response ->
                    sharedViewModel.onLogout()
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
                },
                Response.ErrorListener {
                    Toast.makeText(activity, "Refresh token not successful.", Toast.LENGTH_LONG)
                        .show()
                })
            queue.add(jsonRequest)
        }

        binding.changePassword.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToPasswordFragment())
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.navbar_profile_menu, menu)
        this.menuList = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.saveProfile) {
            val postRequest = PhotoUpdateRequest(API_KEY, token)
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.postNewPost(postRequest, newProfilePicture)
            }
            return true
        }

        if (id == R.id.goHomeButton) {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
        }

        return true
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            val uriPathHelper = URIPathHelper()
            if (uri != null) {
                val filePath = uriPathHelper.getPath(requireActivity().applicationContext, uri)
                binding.profilePicture.setImageURI(uri)
                newProfilePicture = File(filePath)
            }
            viewModel.onImageUpload()
        }
    }

    //Dialog with profile pic actions
    private fun showMessageBox() {

        //Inflate the dialog as custom view
        val messageBoxView =
            LayoutInflater.from(activity).inflate(R.layout.profile_picture_dialog, null)

        //AlertDialogBuilder
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)

        //show dialog
        val messageBoxInstance = messageBoxBuilder.show()

        //set Listener
        messageBoxView.setOnClickListener() {
            //close dialog
            messageBoxInstance.dismiss()
        }

        messageBoxView.buttonUpload.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext()?.let { it1 ->
                        ActivityCompat.checkSelfPermission(
                            it1,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    } ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //permission already granted
                    pickImageFromGallery()
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery()
            }
            messageBoxInstance.dismiss()
        }

        messageBoxView.buttonDelete.setOnClickListener {
            viewModel.deleteUserPicture(token)
            messageBoxInstance.dismiss()
        }

        viewModel.eventUserPictureDeleted.observe(viewLifecycleOwner, Observer { isDeleted ->
            if (isDeleted){

                binding.profilePicture.visibility = View.GONE
                binding.profilePictureAvatar.visibility = View.VISIBLE

                mConstraintSet.clone(mConstraintLayout)
                mConstraintSet.connect(
                    binding.name.id, ConstraintSet.TOP,
                    binding.profilePictureAvatar.id, ConstraintSet.BOTTOM
                )
                mConstraintSet.applyTo(mConstraintLayout)

                sharedViewModel.user.value?.profile = ""

                viewModel.onDeleteUserPictureComplete()
            }
        })
    }
}
