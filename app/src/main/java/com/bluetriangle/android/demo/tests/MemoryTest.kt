package com.bluetriangle.android.demo.tests

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler

class MemoryTest(
    val interval: Long = 30L
) : BTTTestCase {
    override val title: String
        get() = "Memory Test"
    override val description: String
        get() = "Performs memory allocation for $interval secs"

    override fun run(): String? {
        val memoryToAllocate = 20 * 1024 * 1024
        val allocatedMemory = ByteArray(memoryToAllocate)
        allocatedMemory.fill(0)
        Thread {
            Thread.sleep(interval * 1000)
            allocatedMemory.fill(2)
        }.start()
        return null
    }
}