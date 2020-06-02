package com.yasin.trellvideo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yasin.trellvideo.util.Event

/**
 * Created by Yasin on 2/6/20.
 */
class MainViewModel : ViewModel() {

    val navigateToSecondScreen : MutableLiveData<Event<String>> = MutableLiveData()
    val navigateToThirdScreen : MutableLiveData<Event<String>> = MutableLiveData()

    fun navigateToSecond() {
        this.navigateToSecondScreen.value = Event("navigateSecond")
    }

    fun navigateToThird() {
        this.navigateToThirdScreen.value = Event("navigateThird")
    }
}