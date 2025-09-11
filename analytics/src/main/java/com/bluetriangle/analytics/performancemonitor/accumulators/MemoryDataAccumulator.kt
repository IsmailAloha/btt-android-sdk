package com.bluetriangle.analytics.performancemonitor.accumulators

import com.bluetriangle.analytics.performancemonitor.DataPoint

internal class MemoryDataAccumulator(val isVerboseDebug: Boolean): MetricDataAccumulator<DataPoint.MemoryDataPoint> {

    companion object  {
        const val MIN_MEMORY = "minMemory"
        const val MAX_MEMORY = "maxMemory"
        const val TOTAL_MEMORY = "totalMemory"
        const val AVG_MEMORY = "avgMemory"
    }

    private val totalMemory = Runtime.getRuntime().maxMemory()

    override val fields: Map<String, String>
        get() = mapOf(
            MIN_MEMORY to minMemory,
            MAX_MEMORY to maxMemory,
            TOTAL_MEMORY to totalMemory,
            AVG_MEMORY to avgMemory
        ).mapValues { it.value.toString() }

    private var minMemory = Long.MAX_VALUE
    private var maxMemory: Long = 0
    private var cumulativeMemory: Long = 0
    private var memoryCount: Long = 0
    var memoryUsed = arrayListOf<Long>()

    private val avgMemory: Long
        get() = if (memoryCount == 0L) 0 else cumulativeMemory / memoryCount

    override fun accumulate(data: DataPoint.MemoryDataPoint) {
        if (data.memoryUsage < minMemory) {
            minMemory = data.memoryUsage
        }
        if (data.memoryUsage > maxMemory) {
            maxMemory = data.memoryUsage
        }
        if (isVerboseDebug) {
            memoryUsed.add(data.memoryUsage)
        }
        cumulativeMemory += data.memoryUsage
        memoryCount++
    }

}