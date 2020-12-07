package sk.stuba.mobv_team_7.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sk.stuba.mobv_team_7.database.PostsDatabase
import sk.stuba.mobv_team_7.database.asDomainModel
import sk.stuba.mobv_team_7.home.PostDto
import timber.log.Timber

class PostsRepository(private val database: PostsDatabase) {

    val posts: LiveData<List<PostDto>> = Transformations.map(database.postDao.getPosts()) {
        it.asDomainModel()
    }

    suspend fun refreshPosts() {
        withContext(Dispatchers.IO) {
            Timber.d("refresh videos is called");
            val playlist = DevByteNetwork.devbytes.getPlaylist() // call
            database.postDao.insertAll(playlist.asDatabaseModel())
        }
    }

}