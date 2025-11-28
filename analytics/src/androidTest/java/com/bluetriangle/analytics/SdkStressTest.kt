package com.bluetriangle.analytics

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class SdkStressTest {

    // Test configuration parameters
    private val testDurationSeconds = 30 // Run the test for 30 seconds
    private val timerThreads = 100000 // Number of threads constantly creating timers
    private val networkThreads = 100000 // Number of threads constantly submitting network requests
    private val timersPerThreadBurst = 20 // Number of timers to create in a quick burst
    private val networkRequestsPerThreadBurst = 20 // Number of network requests to submit in a burst
    private val heavyMainThreadWork = true // Toggle to simulate main thread blocking
    private val heavyCpuAndMemoryWork = true // Toggle to simulate resource starvation

    private var tracker: Tracker? = null

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val configuration = BlueTriangleConfiguration().apply {
            siteId = "YOUR-TEST-SITE-ID"
        }
        Tracker.init(context as Application, configuration)
        tracker = Tracker.instance
        assertNotNull("Tracker should not be null", tracker)
    }

    @Test
    fun runMultiFeatureStressTest() {
        println("Starting SDK Stress Test for $testDurationSeconds seconds...")
        println("Configuration: $timerThreads timer threads, $networkThreads network threads.")

        val testEndTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(testDurationSeconds.toLong())
        val executor = Executors.newFixedThreadPool(timerThreads + networkThreads + 2) // +2 for CPU/Mem threads
        val latch = CountDownLatch(timerThreads + networkThreads)

        // --- 1. Heavy Timer Usage ---
        repeat(timerThreads) { threadId ->
            executor.submit {
                try {
                    while (System.currentTimeMillis() < testEndTime) {
                        // Create a burst of timers
                        repeat(timersPerThreadBurst) {
                            val timer = Timer("StressTestPage_T$threadId", "StressTestSegment")
                            // Simulate some work
                            Thread.sleep(Random.nextLong(1, 10))
                            timer.submit()
                        }
                        // Brief pause before next burst
                        Thread.sleep(Random.nextLong(50, 150))
                    }
                } finally {
                    latch.countDown()
                    println("Timer thread $threadId finished.")
                }
            }
        }

        // --- 2. Heavy Network Calls (submitCapturedRequest) ---
        repeat(networkThreads) { threadId ->
            executor.submit {
                try {
                    // Create a dummy timer to associate network requests with
                    val associatedTimer = Timer("StressTestPage_Net$threadId", "StressTestSegment").apply { start() }
                    tracker?.setMostRecentTimer(associatedTimer)
                    while (System.currentTimeMillis() < testEndTime) {
                        repeat(networkRequestsPerThreadBurst) {
                            val request = CapturedRequest().apply {
                                url = "https://stress.test/api/v1/data/$it"
                                responseStatusCode = 200
                                host = "stress.test"
                                startTime = System.currentTimeMillis()
                                endTime = System.currentTimeMillis() + Random.nextLong(20, 200)
                            }
                            // This directly calls the method you want to stress
                            tracker?.submitCapturedRequest(request)
                        }
                        Thread.sleep(Random.nextLong(50, 150))
                    }
                } finally {
                    latch.countDown()
                    println("Network thread $threadId finished.")
                }
            }
        }

        // --- 3. Heavy Main Thread Usage ---
        if (heavyMainThreadWork) {
            thread(start = true) {
                while (System.currentTimeMillis() < testEndTime) {
                    InstrumentationRegistry.getInstrumentation().runOnMainSync {
                        // Simulate a heavy UI calculation or file I/O on the main thread
                        Thread.sleep(100) // WARNING: This will make the app unresponsive, which is the point.
                        println("Main thread work executed.")
                    }
                    Thread.sleep(200)
                }
            }
        }

        // --- 4. Heavy CPU and Memory Usage ---
        if (heavyCpuAndMemoryWork) {
            executor.submit {
                val bigList = mutableListOf<ByteArray>()
                while (System.currentTimeMillis() < testEndTime) {
                    // CPU intensive work (e.g., sorting a large array)
                    val array = IntArray(20_000) { Random.nextInt() }
                    array.sort()

                    // Memory intensive work (allocating memory)
                    if (bigList.size < 500) { // Cap to avoid OutOfMemoryError
                        bigList.add(ByteArray(1024 * 1024)) // Allocate 1MB
                    }
                    Thread.sleep(50)
                }
                println("CPU/Memory thread finished.")
            }
        }

        // Wait for all timer and network threads to complete after the duration
        val completed = latch.await(testDurationSeconds + 5L, TimeUnit.SECONDS)

        // Shutdown the executor
        executor.shutdownNow()

        if (!completed) {
            throw AssertionError("Stress test threads did not complete within the timeout.")
        }
        println("SDK Stress Test completed. If no crashes occurred, the test is considered passed.")
    }
}
