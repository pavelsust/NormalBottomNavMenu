package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.adapter.MenuRecyclerViewAdapter


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter =
            MenuRecyclerViewAdapter {
                findNavController().navigate(
                    when (it) {
                        0 -> R.id.action_homeFragment_to_viewPagerFragment
                        1 -> R.id.action_homeFragment_to_viewPager2Fragment
                        else -> R.id.action_homeFragment_to_navControllerFragment
                    }
                )
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}