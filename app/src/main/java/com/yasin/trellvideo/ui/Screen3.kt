package com.yasin.trellvideo.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import com.yasin.trellvideo.R
import com.yasin.trellvideo.databinding.Screen3Binding
import com.yasin.trellvideo.util.VideoCompressor
import com.yasin.trellvideo.viewmodel.MainViewModel

/**
 * Created by Yasin on 2/6/20.
 */
class Screen3 : Fragment(R.layout.screen_3) {

    private lateinit var binding : Screen3Binding
    private val viewModel: MainViewModel by navGraphViewModels(R.id.nav_main) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(
                    VideoCompressor.with(requireActivity())
                ) as T
            }
        }
    }

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
        binding = Screen3Binding.bind(view)
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        observeCompressedFileUri()
        observePlayingStatus()
    }

    private fun observePlayingStatus() {
        viewModel.compressedVideoPlayingStatus.observe(this.viewLifecycleOwner, Observer {
            if(it) {
                binding.video.start()
            }else {
                binding.video.pause()
            }
        })
    }

    private fun observeCompressedFileUri() {
        viewModel.compressedFileUri.observe(this.viewLifecycleOwner, Observer {
            binding.video.setVideoURI(Uri.parse(it.peekContent()))
            binding.video.start()
        })
    }
}