package com.bluetriangle.android.demo.tests

import kotlin.io.path.writeBytes

class IOOperationsTest(val interval: Long = 10L):BTTTestCase {
    override val title: String
        get() = "IO Operation"
    override val description: String
        get() = "Performs file writing operation for $interval secs"

    override fun run(): String? {
        val startTime = System.currentTimeMillis()
        val file = kotlin.io.path.createTempFile("test_file")
        while(System.currentTimeMillis() - startTime <= (interval * 1000)) {
            file.writeBytes(title.toByteArray())
            Thread.sleep(300)
        }
        return null
    }

}