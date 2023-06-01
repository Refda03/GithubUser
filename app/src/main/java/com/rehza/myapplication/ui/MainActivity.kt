package com.rehza.myapplication.ui

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.rehza.myapplication.R
import com.rehza.myapplication.databinding.ActivityMainBinding
import com.rehza.myapplication.datauser.model.SettingPreferences
import com.rehza.myapplication.datauser.response.ItemsItem
import com.rehza.myapplication.adapter.UserAdapter
import com.rehza.myapplication.viewmodel.MainViewModel
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModel.Factory(SettingPreferences(this))
    }
    private var _query = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            _query = savedInstanceState.getString(STATE_NOT_FOUND).toString()
        }

        adapter = UserAdapter()
        adapter.notifyDataSetChanged()

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ItemsItem) {
                Intent(this@MainActivity, DetailUserActivity::class.java).also {
                    it.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(DetailUserActivity.EXTRA_ID, data.id)
                    it.putExtra(DetailUserActivity.EXTRA_AVATAR_URL, data.avatarUrl)
                    startActivity(it)
                }
            }

        })

        binding.apply {
            rvUser.layoutManager = LinearLayoutManager(this@MainActivity)
            rvUser.setHasFixedSize(true)
            rvUser.adapter = adapter
        }

        mainViewModel.searchResult.observe(this) {
            if (it != null) {
                adapter.setItems(it)
                showLoading(false)
                showNotFound(it.isEmpty())
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_NOT_FOUND, _query)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.hint_search)
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            var searchUser: Job? =null
            override fun onQueryTextSubmit(query: String): Boolean {
                searchUser(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchUser?.cancel()
                searchUser = CoroutineScope(Dispatchers.Main).launch {
                    delay(200)
                        if (newText.isNotEmpty()) {
                            mainViewModel.searchUsers(newText)
                        }
                }
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> {
                Intent(this, FavoriteActivity::class.java).also {
                    startActivity(it)
                }
            }
            R.id.menu_settings -> {
                Intent(this, SettingActivity::class.java).also {
                    startActivity(it)
                }
                return true
            }
            else -> return true
        }
        return true
    }

    private fun searchUser(query: String) {
        binding.apply {
            adapter.clear()
            if (query.isEmpty()) return
            showLoading(true)
            showNotFound(false)
            mainViewModel.searchUsers(query)
            _query = query
        }
    }

    private fun showLoading(state: Boolean) {
        binding.givLoading.visibility = if (state) View.VISIBLE else View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun showNotFound(state: Boolean) {
        if (state) {
            binding.tvNotFound.text = "${resources.getString(R.string.not_found)}\"${_query}\""
            binding.tvNotFound.visibility = View.VISIBLE
        } else {
            binding.tvNotFound.visibility = View.GONE
        }
    }
    companion object {
        private const val STATE_NOT_FOUND = "state_not_found"
    }
}