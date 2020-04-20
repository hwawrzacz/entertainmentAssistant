package com.wawrzacz.entertainmentassistant.activity_main.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesBinding

class MoviesFragment: Fragment() {
    private lateinit var binding: FragmentMoviesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_movies, container, false)

        return binding.root
    }
}