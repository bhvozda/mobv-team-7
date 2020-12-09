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

package sk.stuba.mobv_team_7.network

import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import sk.stuba.mobv_team_7.http.URL_RETROFIT

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

/**
 * A retrofit service to fetch a devbyte playlist.
 */
interface PlaylistService {
    @Headers(
        "Content-Type: Application/Json",
        "Accept: Application/Json",
        "Cache-Control: no-cache"
    )
    @POST("service.php")
    suspend fun getPosts(@Body body: RequestBody): List<NetworkPost>
}

object PostNetwork {

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(URL_RETROFIT)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val mobv = retrofit.create(PlaylistService::class.java)

}



