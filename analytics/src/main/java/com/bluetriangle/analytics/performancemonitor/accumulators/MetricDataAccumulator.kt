package com.bluetriangle.analytics.performancemonitor.accumulators

import com.bluetriangle.analytics.performancemonitor.DataPoint

interface MetricDataAccumulator<T: DataPoint> {

    val fields: Map<String, String>

    fun accumulate(data: T)

}