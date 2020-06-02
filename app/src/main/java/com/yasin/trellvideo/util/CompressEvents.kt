package com.yasin.trellvideo.util

/**
 * Created by Yasin on 2/6/20.
 */
sealed class CompressEvents {
    object OnStart : CompressEvents()
    object OnFinish : CompressEvents()
    object OnSuccess : CompressEvents()
    object AlreadyCompressed : CompressEvents()
    data class OnError(val error : String) : CompressEvents()
}