package org.stepik.core.auth.webkit.network

internal class ExtendedTime
private constructor(private val baseTime: Long, private val subtime: Int) : Comparable<ExtendedTime> {

    fun baseTime(): Long {
        return baseTime
    }

    fun incrementSubtime(): ExtendedTime {
        return ExtendedTime(baseTime, subtime + 1)
    }

    override fun compareTo(other: ExtendedTime): Int {
        val d = baseTime.compareTo(other.baseTime)
        return if (d != 0) d else subtime.compareTo(other.subtime)
    }

    override fun toString(): String {
        return "[baseTime=$baseTime, subtime=$subtime]"
    }

    companion object {

        fun currentTime(): ExtendedTime {
            return ExtendedTime(System.currentTimeMillis(), 0)
        }
    }
}
