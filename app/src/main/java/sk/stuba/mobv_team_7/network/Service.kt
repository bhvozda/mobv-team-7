/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.devbyteviewer.network

import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import sk.stuba.mobv_team_7.data.User
import sk.stuba.mobv_team_7.home.PostDto
import sk.stuba.mobv_team_7.http.API_KEY
import sk.stuba.mobv_team_7.http.JsonObjectRequestModified
import sk.stuba.mobv_team_7.http.URL

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

/**
 * A retrofit service to fetch a devbyte playlist.
 */
//interface PlaylistService {
//    @GET("")
//    suspend fun getPlaylist(): NetworkVideoContainer
//}

/**
 * Main entry point for network access. Call like `DevByteNetwork.devbytes.getPlaylist()`
 */
//object DevByteNetwork {
//
//    // Configure retrofit to parse JSON and use coroutines
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(URL)
//        .addConverterFactory(MoshiConverterFactory.create())
//        .build()
//
//    val devbytes = retrofit.create(PlaylistService::class.java)
//
//}

private fun getAllPosts(user: User) {
    val token = user.token.toString()
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
//                binding.swipeRefreshLayout.isRefreshing = false
        },
        Response.ErrorListener {
            Toast.makeText(activity, "Unexpected error occurred.", Toast.LENGTH_LONG).show()
//                binding.swipeRefreshLayout.isRefreshing = false
        })
    queue.add(jsonRequest)
}


