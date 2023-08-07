package com.bluetriangle.android.demo.kotlin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluetriangle.analytics.Timer
import com.bluetriangle.android.demo.tests.BTTTestCase
import com.bluetriangle.android.demo.tests.IOOperationsTest
import com.bluetriangle.android.demo.tests.InfiniteLoopTest
import com.bluetriangle.android.demo.tests.WordGeneratorTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CPUTestViewModel : ViewModel() {

    val cpuTask = MutableLiveData(CPUTask.InfiniteLoop)
    val cpuThread = MutableLiveData(CPUThread.Main)

    enum class CPUTask(val testCase: BTTTestCase) {
        InfiniteLoop(InfiniteLoopTest()),
        WordGenerator(WordGeneratorTest("unconscious")),
        IOOperations(IOOperationsTest())
    }

    enum class CPUThread(val dispatcher: CoroutineDispatcher) {
        Main(Dispatchers.Main),
        Background(Dispatchers.Default)
    }

    var isTimerStarted = MutableLiveData(false)

    private var timer: Timer? = null

    fun onTimerButtonClick() {
        val isStarted = isTimerStarted.value?:return
        isTimerStarted.value = !isStarted
        if(isStarted) {
            timer?.submit()
        } else {
            timer = Timer()
            timer?.setPageName("CPU Usage")
            timer?.start()
        }
    }

    fun onTaskChange(pos: Int) {
        cpuTask.value = CPUTask.values()[pos]
    }

    fun onThreadChange(pos: Int) {
        cpuThread.value = CPUThread.values()[pos]
    }

    fun onRunTaskClicked() {
        val task = cpuTask.value ?: return
        val thread = cpuThread.value ?: return

        if (thread == CPUThread.Main) {
            task.testCase.run()
        } else {
            viewModelScope.launch(thread.dispatcher) {
                task.testCase.run()
            }
        }
    }

}