package com.example.myapplication.ui.samples.childFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.example.myapplication.NormalBottomBar
import com.example.myapplication.R

class FragmentHome : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        var view = inflater.inflate(R.layout.fragment_1, container, false)
        view.findViewById<AppCompatTextView>(R.id.text_home).setOnClickListener {
            logout()
        }
        return view
    }
    fun logout(){
        val intent = Intent(requireActivity(), NormalBottomBar::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}