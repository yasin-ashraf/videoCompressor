package com.yasin.trellvideo.ui

import android.content.res.Configuration
import android.net.Uri
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

    override fun onPause() {
        super.onPause()
        if(binding.video.isPlaying) {
            viewModel.setLastPlayedPosition(binding.video.currentPosition)
            binding.video.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.videoLastPlayedPosition.value != 0) {
            binding.video.seekTo(viewModel.videoLastPlayedPosition.value ?: 0)
            binding.video.start()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = Screen2Binding.bind(view)
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        observeNavigationEvent()
        observeFileUri()
    }

    private fun observeFileUri() {
        viewModel.selectedFileUri.observe(this.viewLifecycleOwner, Observer {
            if(it != null) {
                binding.video.setVideoURI(Uri.parse(it))
                if(viewModel.videoLastPlayedPosition.value != 0) {
                    binding.video.seekTo(viewModel.videoLastPlayedPosition.value ?: 0)
                }else {
                    binding.video.start()
                }
            }
        })
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