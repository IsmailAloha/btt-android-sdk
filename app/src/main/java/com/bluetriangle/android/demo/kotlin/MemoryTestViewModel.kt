package com.bluetriangle.android.demo.kotlin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluetriangle.analytics.Timer
import com.bluetriangle.android.demo.tests.BTTTestCase
import com.bluetriangle.android.demo.tests.MemoryTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoryTestViewModel : ViewModel() {

    private var timer: Timer? = null
    var isTimerStarted = MutableLiveData(false)

    fun onTimerButtonClick() {
        val isStarted = isTimerStarted.value ?: return
        isTimerStarted.value = !isStarted
        if (isStarted) {
            timer?.submit()
        } else {
            timer = Timer()
            timer?.setPageName("Memory Usage")
            timer?.start()
        }
    }

    fun onRunTaskClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            MemoryTest().run()
        }
    }
}