package sk.stuba.mobv_team_7.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import sk.stuba.mobv_team_7.database.PostsDatabase
import sk.stuba.mobv_team_7.database.asDomainModel
import sk.stuba.mobv_team_7.home.PostDto
import sk.stuba.mobv_team_7.network.PostNetwork
import sk.stuba.mobv_team_7.network.asDatabaseModel

class PostsRepository(private val database: PostsDatabase) {

    val posts: LiveData<List<PostDto>> = Transformations.map(database.postDao.getPosts()) {
        it.asDomainModel()
    }

    suspend fun refreshPosts(requestBody: RequestBody) {
        withContext(Dispatchers.IO) {
            val playlist = PostNetwork.mobv.getPosts(requestBody)
            database.postDao.insertAll(playlist.asDatabaseModel())
        }
    }

}