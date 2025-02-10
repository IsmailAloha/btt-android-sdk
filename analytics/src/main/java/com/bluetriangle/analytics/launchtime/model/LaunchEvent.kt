package com.bluetriangle.analytics.launchtime.model

internal sealed class LaunchEvent private constructor(val data: LaunchData) {

    constructor(activityName:String, startTime:Long, type: LaunchType):this(LaunchData(activityName, startTime, System.currentTimeMillis(), type))

    class HotLaunch(activityName:String, startTime:Long) : LaunchEvent(activityName, startTime,
        LaunchType.Hot
    )

    class WarmLaunch(activityName:String, startTime:Long) : LaunchEvent(activityName, startTime,
        LaunchType.Warm
    )

    class ColdLaunch(activityName: String, startTime: Long) : LaunchEvent(activityName, startTime,
        LaunchType.Cold
    )
    companion object {
        fun create(type: LaunchType, activityName: String, startTime: Long): LaunchEvent {
            return when(type) {
                LaunchType.Hot -> HotLaunch(activityName, startTime)
                LaunchType.Warm -> WarmLaunch(activityName, startTime)
                LaunchType.Cold -> ColdLaunch(activityName, startTime)
            }
        }
    }

}