package com.rehza.myapplication.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rehza.myapplication.R
import com.rehza.myapplication.databinding.FragmentFollowersBinding
import com.rehza.myapplication.adapter.FollowersAdapter
import com.rehza.myapplication.viewmodel.FollowersViewModel

class FollowersFragment : Fragment() {

    private var _binding : FragmentFollowersBinding? = null
    private val binding get() = _binding!!
    private val followViewModel by viewModels<FollowersViewModel>()
    private lateinit var adapter: FollowersAdapter
    private lateinit var username: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_followers, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = arguments?.getInt(ARG_SECTION_NUMBER)
        val args = arguments
        username = args?.getString(DetailUserActivity.EXTRA_USERNAME).toString()
        _binding = FragmentFollowersBinding.bind(view)

        adapter = FollowersAdapter()
        adapter.notifyDataSetChanged()

        binding.apply {
            rvUser.setHasFixedSize(true)
            rvUser.layoutManager = LinearLayoutManager(activity)
            rvUser.adapter = adapter
        }

        showLoading(true)
        when (index) {
            0 -> followViewModel.setListFollowers(username)
            1 -> followViewModel.setListFollowing(username)
        }

        followViewModel.listFollow.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.setItems(it)
                showLoading(false)
            }
            showNotFound(it.isEmpty())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(state: Boolean){
        binding.givLoading.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showNotFound(state: Boolean){
        if (state) {
            binding.tvNotFound.visibility = View.VISIBLE
        } else {
            binding.tvNotFound.visibility = View.GONE
        }
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        const val EXTRA_USERNAME = "extra_username"
    }
}