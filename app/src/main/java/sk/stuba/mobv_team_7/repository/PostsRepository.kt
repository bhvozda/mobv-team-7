package sk.stuba.mobv_team_7.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.devbyteviewer.network.PostNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.stuba.mobv_team_7.api.GithubApi
import sk.stuba.mobv_team_7.api.PostResponse
import sk.stuba.mobv_team_7.database.PostsDatabase
import sk.stuba.mobv_team_7.database.asDomainModel
import sk.stuba.mobv_team_7.home.PostDto
import sk.stuba.mobv_team_7.network.asDatabaseModel
import timber.log.Timber

class PostsRepository(private val database: PostsDatabase) {

    val posts: LiveData<List<PostDto>> = Transformations.map(database.postDao.getPosts()) {
        it.asDomainModel()
    }

    suspend fun refreshPosts(requestBody: RequestBody) {
        withContext(Dispatchers.IO) {
            Timber.d("refresh posts is called");
            val playlist = PostNetwork.mobv.getPosts(requestBody) // call
            database.postDao.insertAll(playlist.asDatabaseModel())
        }
    }

}