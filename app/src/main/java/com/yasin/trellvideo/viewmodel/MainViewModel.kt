package com.yasin.trellvideo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yasin.trellvideo.VideoCompressor
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
    private val selectedFile : MutableLiveData<File> = MutableLiveData()
    val videoLastPlayedPosition : MutableLiveData<Int> = MutableLiveData(0)
    val bitRate : MutableLiveData<String> = MutableLiveData()
    val compressionStatusText : MutableLiveData<String> = MutableLiveData()
    private val _compressionStatus : MutableLiveData<Event<CompressEvents>> = MutableLiveData()
    val compressionStatus : LiveData<Event<CompressEvents>> = _compressionStatus
    private val _compressedFileUri : MutableLiveData<Event<String>> = MutableLiveData()
    val compressedFileUri : LiveData<Event<String>> get() = _compressedFileUri
    val compressedVideoPlayingStatus : MutableLiveData<Boolean> = MutableLiveData()
    val compressedVideoPlayingStatusText : MutableLiveData<String> = MutableLiveData("Pause")

    fun setSelectFileEvent() {
        _compressionStatus.value = Event(CompressEvents.OnStart) //set anything other than success / alreadyCompressed
        this.selectVideoFileEvent.value = Event("selectFile")
    }

    fun compressFile() {
        //compress video
        if(_compressionStatus.value?.peekContent() !in arrayOf(CompressEvents.OnSuccess, CompressEvents.AlreadyCompressed)) {
            videoCompressor
                .setFile(selectedFile.value ?: return)
                .setBitRate(bitRate.value.toString() + "k")
                .setOutputPath(FileUtils.outputPath)
                .setOutputFileName(Random().nextFloat().toString() + ".mp4")
                .setCallback(ffMpegCallback)
                .compress()
            this.compressionStatusText.value = "Compressing..."
        }else {
            _compressionStatus.value = Event(CompressEvents.AlreadyCompressed)
        }
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

    fun toggleVideoPlaying(status : Boolean) {
        if(status) compressedVideoPlayingStatusText.value = "Play"
        else compressedVideoPlayingStatusText.value = "Pause"
        this.compressedVideoPlayingStatus.value = !status
    }

    private val ffMpegCallback : FFMpegCallback = object : FFMpegCallback {
        override fun onProgress(progress: String) {
            _compressionStatus.value = Event(CompressEvents.OnStart)
        }

        override fun onSuccess(convertedFile: File) {
            compressionStatusText.value = "File Compressed"
            _compressedFileUri.value = Event(FileUtils.getUri(convertedFile).toString())
            _compressionStatus.value = Event(CompressEvents.OnSuccess)
        }

        override fun onFailure(error: Exception) {
            _compressionStatus.value = Event(CompressEvents.OnError(error.toString()))
            compressionStatusText.value = "File Compression Failed."
        }

        override fun onNotAvailable(error: Exception) {
            _compressionStatus.value = Event(CompressEvents.OnError(error.toString()))
        }

        override fun onFinish() {

        }
    }
}