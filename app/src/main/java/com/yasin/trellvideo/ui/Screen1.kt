package com.yasin.trellvideo.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.yasin.trellvideo.R
import com.yasin.trellvideo.databinding.Screen1Binding
import com.yasin.trellvideo.util.FileUtils
import com.yasin.trellvideo.util.VideoCompressor
import com.yasin.trellvideo.viewmodel.MainViewModel

/**
 * Created by Yasin on 2/6/20.
 */
class Screen1 : Fragment(R.layout.screen_1) {

    private lateinit var binding: Screen1Binding
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = Screen1Binding.bind(view)
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        observeNavigationEvent()
    }

    private fun observeNavigationEvent() {
        viewModel.selectVideoFileEvent.observe(this.viewLifecycleOwner, Observer {
            if (!it.hasBeenHandled) {
                it.getContentIfNotHandled() // mark as handled
                if (checkStoragePermissions()) {
                    pickMediaFromGallery()
                }
            }
        })
    }

    private fun checkStoragePermissions(): Boolean {
        val permissionStorageRead = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val permissionStorageWrite = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionStorageRead != PackageManager.PERMISSION_GRANTED || permissionStorageWrite != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission()
            return false
        }
        return true
    }

    private fun requestStoragePermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            REQUEST_PERMISSIONS_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS_STORAGE -> {
                if (grantResults.isEmpty()
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Permission Denied. Need storage access to upload video.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    pickMediaFromGallery()
                }
            }
        }
    }

    private fun pickMediaFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*"))
        }
        startActivityForResult(intent, RC_MEDIA_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {

            RC_MEDIA_PICKER -> {

                data?.data?.also { uri ->
                    requireContext().contentResolver.query(uri, null, null, null, null)?.use {
                        it.moveToFirst()
                        it.close()
                    }
                    viewModel.setSelectedVideoUriPath(uri.toString())

                    val file = FileUtils.getFile(requireContext(), uri)
                    Log.d("FILE", file?.absolutePath ?: "No path")
                    viewModel.setVideoFile(file ?: return)
                    navigateToScreen2()
                    Toast.makeText(requireContext(), uri.toString(), Toast.LENGTH_SHORT).show()
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun navigateToScreen2() {
        findNavController().navigate(R.id.action_screen1_to_screen2)
    }

    companion object {
        private const val REQUEST_PERMISSIONS_STORAGE = 1000
        private const val RC_MEDIA_PICKER = 900
    }


}