package com.yasin.trellvideo

import android.content.Context
import android.util.Log
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.yasin.trellvideo.util.FFMpegCallback
import java.io.File
import java.io.IOException

/**
 * Created by Yasin on 2/6/20.
 */
class VideoCompressor private constructor(private val context: Context) {

    private var video: File? = null
    private var callback: FFMpegCallback? = null
    private var outputPath = ""
    private var outputFileName = ""
    private var bitRate = ""

    fun setFile(originalFiles: File): VideoCompressor {
        this.video = originalFiles
        return this
    }

    fun setCallback(callback: FFMpegCallback): VideoCompressor {
        this.callback = callback
        return this
    }

    fun setOutputPath(output: String): VideoCompressor {
        this.outputPath = output
        return this
    }

    fun setOutputFileName(output: String): VideoCompressor {
        this.outputFileName = output
        return this
    }

    fun setBitRate(output: String): VideoCompressor {
        this.bitRate = output
        return this
    }

    fun compress() {
        if (video == null || !video!!.exists()) {
            callback!!.onFailure(IOException("File not exists"))
            return
        }
        if (!video!!.canRead()) {
            callback!!.onFailure(IOException("Can't read the file. Missing permission?"))
            return
        }

        val outputLocation = getConvertedFile(outputPath, outputFileName)

        val cmd = arrayOf(
            "-y",
            "-i", video!!.path,
            "-s", "480x360",
            "-r", "25", //frame rate
            "-vcodec", "mpeg4", //codec
            "-b:v", bitRate, //video bitrate
            "-b:a", "48000", //audio bit rate
            "-ac", "2", // audio channels
            "-ar", "22050", // audio sampling rate
            outputLocation.path
        )

        Log.d("COMMAND", cmd.contentToString())

        try {
            FFmpeg.getInstance(context)
                .execute(cmd, object : ExecuteBinaryResponseHandler() {
                override fun onStart() {}

                override fun onProgress(message: String?) {
                    callback!!.onProgress(message!!)
                }

                override fun onSuccess(message: String?) {
                    callback!!.onSuccess(outputLocation)

                }

                override fun onFailure(message: String?) {
                    if (outputLocation.exists()) {
                        outputLocation.delete()
                    }
                    callback!!.onFailure(IOException(message))
                }

                override fun onFinish() {
                    callback!!.onFinish()
                }
            })
        } catch (e: Exception) {
            callback!!.onFailure(e)
        } catch (e2: FFmpegCommandAlreadyRunningException) {
            callback!!.onNotAvailable(e2)
        }

    }

    private fun getConvertedFile(folder: String, fileName: String): File {
        val f = File(folder)

        if (!f.exists())
            f.mkdirs()

        return File(f.path + File.separator + fileName)
    }

    companion object {
        val TAG = "VideoCompresser"
        fun with(context: Context): VideoCompressor {
            return VideoCompressor(context)
        }
    }
}