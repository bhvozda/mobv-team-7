package sk.stuba.mobv_team_7.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.home_fragment.*
import org.json.JSONArray
import org.json.JSONObject
import sk.stuba.mobv_team_7.R
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.databinding.HomeFragmentBinding
import sk.stuba.mobv_team_7.http.API_KEY
import sk.stuba.mobv_team_7.http.DATE_FORMAT_RESPONSE
import sk.stuba.mobv_team_7.http.JsonObjectRequestModified
import sk.stuba.mobv_team_7.http.URL
import sk.stuba.mobv_team_7.posts.PostsAdapter
import sk.stuba.mobv_team_7.shared.SharedViewModel
import java.text.SimpleDateFormat

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private var token: String = ""

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
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this

        binding.videoButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToVideoFragment())
        }

        sharedViewModel.eventLoginSuccessful.observe(viewLifecycleOwner, Observer { user ->
            getAllPosts(user)
            binding.swipeRefreshLayout.setOnRefreshListener {
                getAllPosts(user)
            }
        })

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

    private fun jsonToPostDto(jsonObject: JSONObject): PostDto {
        val postId = jsonObject.get("postid").toString()
        val createdAt = SimpleDateFormat(DATE_FORMAT_RESPONSE).parse(jsonObject.get("created").toString())
        val videoUrl = jsonObject.get("videourl").toString()
        val username = jsonObject.get("username").toString()
        val profile = jsonObject.get("profile").toString()
        return PostDto(postId, createdAt, videoUrl, username, profile)
    }

    private fun putPostsInView(posts: List<PostDto>) {
        val adapter = PostsAdapter(posts) { post ->
            sharedViewModel.onPostChoice(post)
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToVideoPlayerFragment())
        }

        postsRecycleView.adapter = adapter
        postsRecycleView.layoutManager = LinearLayoutManager(this.context)
    }

    private fun getAllPosts(user: User) {
        token = user.token.toString()
        val jsonObject = JSONObject()
        jsonObject.put("action", "posts")
        jsonObject.put("apikey", API_KEY)
        jsonObject.put("token", token)

        val queue = Volley.newRequestQueue(this.context)
        val jsonRequest = JsonObjectRequestModified(
            Request.Method.POST,
            URL,
            jsonObject,
            Response.Listener<JSONArray> { posts ->
                val postsList = mutableListOf<PostDto>()
                for (i in 0 until posts.length()) {
                    val jsonPost = posts.get(i)
                    postsList.add(jsonToPostDto(jsonPost as JSONObject))
                }
                putPostsInView(postsList)
                binding.swipeRefreshLayout.isRefreshing = false
            },
            Response.ErrorListener {
                // TODO: crash analytics
                Toast.makeText(activity, "Unexpected error occurred.", Toast.LENGTH_LONG).show()
                binding.swipeRefreshLayout.isRefreshing = false
            })
        queue.add(jsonRequest)
    }

}