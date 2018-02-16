package org.stepik.core.auth.webkit.network

/**
 * An extended time consisting of a long "base time" and
 * an integer "subtime".
 */
internal class ExtendedTime
/**
 * Creates a new `ExtendedTime`.
 */
private constructor(private val baseTime: Long, private val subtime: Int) : Comparable<ExtendedTime> {

    /**
     * Returns the base time.
     */
    fun baseTime(): Long {
        return baseTime
    }

    /**
     * Returns the subtime.
     */
    fun subtime(): Int {
        return subtime
    }

    /**
     * Increments the subtime and returns the result as a new extended time.
     */
    fun incrementSubtime(): ExtendedTime {
        return ExtendedTime(baseTime, subtime + 1)
    }

    /**
     * {@inheritDoc}
     */
    override fun compareTo(other: ExtendedTime): Int {
        val d = (baseTime - other.baseTime).toInt()
        return if (d != 0) {
            d
        } else subtime - other.subtime
    }

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        return "[baseTime=$baseTime, subtime=$subtime]"
    }

    companion object {

        /**
         * Returns the current extended time with the base time initialized
         * to System.currentTimeMillis() and the subtime initialized to zero.
         */
        fun currentTime(): ExtendedTime {
            return ExtendedTime(System.currentTimeMillis(), 0)
        }
    }
}
