package com.yasin.trellvideo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yasin.trellvideo.util.*
import java.io.File
import java.util.*

/**
 * Created by Yasin on 2/6/20.
 */
class MainViewModel constructor(
    private val videoCompressor: VideoCompressor
): ViewModel() {

    val selectVideoFileEvent : MutableLiveData<Event<String>> = MutableLiveData()
    val selectedFileUri : MutableLiveData<String> = MutableLiveData()
    val selectedFile : MutableLiveData<File> = MutableLiveData()
    val videoLastPlayedPosition : MutableLiveData<Int> = MutableLiveData(0)
    val bitRate : MutableLiveData<String> = MutableLiveData()
    val compressionStatusText : MutableLiveData<String> = MutableLiveData()
    private val _compressionStatus : MutableLiveData<CompressEvents> = MutableLiveData()
    private val _compressedFileUri : MutableLiveData<Event<String>> = MutableLiveData()
    val compressedFileUri : LiveData<Event<String>> get() = _compressedFileUri

    fun setSelectFileEvent() {
        this.selectVideoFileEvent.value = Event("selectFile")
    }

    fun compressFile() {
        //compress video
        videoCompressor
            .setFile(selectedFile.value ?: return)
            .setBitRate(bitRate.value.toString() + "k")
            .setOutputPath(FileUtils.outputPath)
            .setOutputFileName(Random().nextFloat().toString() + ".mp4")
            .setCallback(ffMpegCallback)
            .compress()
        this.compressionStatusText.value = "Compressing..."
    }

    fun setSelectedVideoUriPath(uriPath : String) {
        this.selectedFileUri.value = uriPath
    }

    fun setVideoFile(file: File) {
        this.selectedFile.value = file
    }

    fun setLastPlayedPosition(position : Int) {
        this.videoLastPlayedPosition.postValue(position)
    }

    private val ffMpegCallback : FFMpegCallback = object : FFMpegCallback {
        override fun onProgress(progress: String) {
            _compressionStatus.value = CompressEvents.OnStart
        }

        override fun onSuccess(convertedFile: File) {
            _compressionStatus.value = CompressEvents.OnSuccess
            compressionStatusText.value = "File Compressed"
            _compressedFileUri.value = Event(FileUtils.getUri(convertedFile).toString())
        }

        override fun onFailure(error: Exception) {
            _compressionStatus.value = CompressEvents.onError(error.toString())
            compressionStatusText.value = "File Compression Failed."
        }

        override fun onNotAvailable(error: Exception) {
            _compressionStatus.value = CompressEvents.onError(error.toString())
        }

        override fun onFinish() {
            _compressionStatus.value = CompressEvents.OnFinish
        }
    }
}