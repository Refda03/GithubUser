package com.rehza.myapplication.api

import com.rehza.myapplication.datauser.response.DetailUserResponse
import com.rehza.myapplication.datauser.response.FollowResponseItem
import com.rehza.myapplication.datauser.response.GithubResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/search/users")
    fun searchUsers(
        @Query("q") query: String
    ): Call<GithubResponse>

    @GET("users/{username}")
    fun getDetail(
        @Path("username") username: String
    ): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    fun getFollowers(
        @Path("username") username: String
    ): Call<ArrayList<FollowResponseItem>>

    @GET("users/{username}/following")
    fun getFollowing(
        @Path("username") username: String
    ): Call<ArrayList<FollowResponseItem>>
}