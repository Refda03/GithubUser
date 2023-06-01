package com.rehza.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rehza.myapplication.api.ApiConfig
import com.rehza.myapplication.datauser.response.FollowResponseItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowersViewModel : ViewModel() {
    private val _listFollow = MutableLiveData<ArrayList<FollowResponseItem>>()
    val listFollow: LiveData<ArrayList<FollowResponseItem>> = _listFollow

    private val _searchError = MutableLiveData<Throwable>()

    fun setListFollowers(username: String) {
        val client = ApiConfig.getApiService().getFollowers(username)
        setList(client)
    }

    fun setListFollowing(username: String) {
        val client = ApiConfig.getApiService().getFollowing(username)
        setList(client)
    }

    private fun setList(client: Call<ArrayList<FollowResponseItem>>) {
        client.enqueue(object : Callback<ArrayList<FollowResponseItem>> {
            override fun onResponse(
                call: Call<ArrayList<FollowResponseItem>>,
                response: Response<ArrayList<FollowResponseItem>>
            ) {
                if (response.isSuccessful) {
                    _listFollow.postValue(response.body())
                } else {
                    _searchError.value =
                        Throwable("Failed to get users: ${response.code()} ${response.message()}")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<FollowResponseItem>>, t: Throwable) {
                _searchError.value = t
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
    companion object {
        private const val TAG = "FollowersViewModel"
    }
}