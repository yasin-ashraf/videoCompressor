package com.yasin.trellvideo.util

import java.io.File

interface FFMpegCallback {

    fun onProgress(progress: String)

    fun onSuccess(convertedFile: File)

    fun onFailure(error: Exception)

    fun onNotAvailable(error: Exception)

    fun onFinish()

}