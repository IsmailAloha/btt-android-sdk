package com.bluetriangle.analytics.performancemonitor.accumulators

import com.bluetriangle.analytics.performancemonitor.DataPoint

internal class CPUDataAccumulator(val isVerboseDebug: Boolean): MetricDataAccumulator<DataPoint.CPUDataPoint> {

    companion object {
        const val MIN_CPU = "minCPU"
        const val MAX_CPU = "maxCPU"
        const val AVG_CPU = "avgCPU"
    }

    override val fields: Map<String, String>
        get() = mapOf(
            MIN_CPU to minCpu,
            MAX_CPU to maxCpu,
            AVG_CPU to avgCpu
        ).mapValues { it.value.toString() }

    private var minCpu = Double.MAX_VALUE
    private var maxCpu = 0.0
    private var cumulativeCpu = 0.0
    private var cpuCount: Long = 0
    var cpuUsed = arrayListOf<Double>()

    private val avgCpu: Double
        get() = if (cpuCount == 0L) 0.0 else cumulativeCpu / cpuCount

    override fun accumulate(data: DataPoint.CPUDataPoint) {
        if (data.cpuUsage < minCpu) {
            minCpu = data.cpuUsage
        }
        if (data.cpuUsage > maxCpu) {
            maxCpu = data.cpuUsage
        }
        if (isVerboseDebug) {
            cpuUsed.add(data.cpuUsage)
        }
        cumulativeCpu += data.cpuUsage
        cpuCount++
    }

}