package com.example.myapplication.ui.samples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentViewPagerBinding
import com.example.myapplication.ui.adapter.ViewPagerAdapter
import com.example.myapplication.utils.PresentationViewPager
import com.example.myapplication.utils.setupViewPager

class ViewPagerFragment : Fragment() {
    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewpager.apply {
            setDurationScroll(PresentationViewPager.PRESENTATION_MODE_SCROLL_DURATION)
            adapter = ViewPagerAdapter(childFragmentManager)

        }
        binding.bubbleTabBar.setupViewPager(binding.viewpager)


        binding.bubbleTabBar.addBubbleListener { id ->
            when (id) {
                R.id.home -> binding.viewpager.currentItem = 0
                R.id.log -> binding.viewpager.currentItem = 1
                R.id.doc -> binding.viewpager.currentItem = 2
                R.id.setting -> binding.viewpager.currentItem = 3
            }
        }
    }
}