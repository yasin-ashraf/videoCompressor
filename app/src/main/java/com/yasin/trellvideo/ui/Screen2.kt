package com.yasin.trellvideo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.yasin.trellvideo.R
import com.yasin.trellvideo.databinding.Screen2Binding
import com.yasin.trellvideo.viewmodel.MainViewModel

/**
 * Created by Yasin on 2/6/20.
 */
class Screen2 : Fragment(R.layout.screen_2) {

    private lateinit var binding : Screen2Binding
    private val viewModel : MainViewModel by navGraphViewModels(R.id.nav_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = Screen2Binding.bind(view)
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        observeNavigationEvent()
    }

    private fun observeNavigationEvent() {
        viewModel.navigateToThirdScreen.observe(this.viewLifecycleOwner, Observer {
            if(!it.hasBeenHandled) {
                it.getContentIfNotHandled()
                findNavController().navigate(R.id.action_screen2_to_screen3)
            }
        })

    }
}