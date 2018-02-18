package org.stepik.core.auth.webkit.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import org.stepik.core.common.Loggable
import java.util.*

/**
 * A cookie store.
 */
internal class CookieStore : Loggable {

    private val bucketsType = object : TypeToken<Map<String, Set<Cookie>>>() {

    }.type
    private val gson = GsonBuilder().create()

    /**
     * The mapping from domain names to cookie buckets.
     * Each cookie bucket stores the cookies associated with the
     * corresponding domain. Each cookie bucket is represented
     * by a Map<Cookie></Cookie>,Cookie> to facilitate retrieval of a cookie
     * by another cookie with the same name, domain, and path.
     */
    private val buckets: MutableMap<String, MutableMap<Cookie, Cookie>>

    /**
     * The total number of cookies currently in the store.
     */
    private var totalCount = 0

    /**
     * Creates a new `CookieStore`.
     */

    init {
        buckets = loadCookies()
        buckets.forEach { _, value -> this.totalCount += value.size }
    }

    private fun loadCookies(): MutableMap<String, MutableMap<Cookie, Cookie>> {
        val attributes: CredentialAttributes
        val serviceName = this.javaClass.name
        attributes = CredentialAttributes(serviceName, "cookies", javaClass, false)
        val credentials = PasswordSafe.getInstance().get(attributes)
        val json = credentials?.getPasswordAsString()

        var cookies: Map<String, Set<Cookie>> = HashMap()
        if (json != null) {
            try {
                cookies = gson.fromJson(json, bucketsType)
            } catch (e: JsonSyntaxException) {
                logger.warn(e)
            }

        }

        val buckets = HashMap<String, MutableMap<Cookie, Cookie>>()

        cookies.forEach { key, value ->
            val bucket = HashMap<Cookie, Cookie>()
            value.forEach { cookie -> bucket[cookie] = cookie }
            buckets[key] = bucket
        }

        return buckets
    }

    /**
     * Returns the currently stored cookie with the same name, domain, and
     * path as the given cookie.
     */
    operator fun get(cookie: Cookie): Cookie? {
        val bucket = buckets[cookie.getDomain()] ?: return null
        val storedCookie = bucket[cookie] ?: return null
        if (storedCookie.hasExpired()) {
            bucket.remove(storedCookie)
            totalCount--
            return null
        }
        return storedCookie
    }

    /**
     * Returns all the currently stored cookies that match the given query.
     */
    operator fun get(hostname: String, path: String?, secureProtocol: Boolean, httpApi: Boolean): List<Cookie> {
        val result = ArrayList<Cookie>()

        var domain = hostname
        while (domain.isNotEmpty()) {
            val bucket = buckets[domain]
            if (bucket != null) {
                find(result, bucket, hostname, path, secureProtocol, httpApi)
            }
            val nextPoint = domain.indexOf('.')
            if (nextPoint != -1) {
                domain = domain.substring(nextPoint + 1)
            } else {
                break
            }
        }

        result.sortWith(GetComparator())

        val currentTime = System.currentTimeMillis()
        for (cookie in result) {
            cookie.lastAccessTime = currentTime
        }

        return result
    }

    /**
     * Finds all the cookies that are stored in the given bucket and
     * match the given query.
     */
    private fun find(
            list: MutableList<Cookie>,
            bucket: MutableMap<Cookie, Cookie>,
            hostname: String,
            path: String?,
            secureProtocol: Boolean,
            httpApi: Boolean) {
        val it = bucket.values.iterator()
        while (it.hasNext()) {
            val cookie = it.next()
            if (cookie.hasExpired()) {
                it.remove()
                totalCount--
                continue
            }

            if (cookie.hostOnly) {
                if (!hostname.equals(cookie.getDomain(), ignoreCase = true)) {
                    continue
                }
            } else {
                if (Cookie.domainNotMatches(hostname, cookie.getDomain())) {
                    continue
                }
            }

            if (!Cookie.pathMatches(path, cookie.path)) {
                continue
            }

            if (cookie.secureOnly && !secureProtocol) {
                continue
            }

            if (cookie.httpOnly && !httpApi) {
                continue
            }

            list.add(cookie)
        }
    }

    fun removeAll() {
        totalCount = 0
        buckets.clear()
    }

    fun save() {
        val attributes: CredentialAttributes
        val serviceName = javaClass.name
        attributes = CredentialAttributes(serviceName, "cookies", javaClass, false)
        val cookies = HashMap<String, Set<Cookie>>()
        buckets.forEach { key, value -> cookies[key] = HashSet(value.values) }
        val serialized = gson.toJson(cookies, bucketsType)
        val credentials = Credentials(attributes.userName, serialized)
        PasswordSafe.getInstance().set(attributes, credentials)
    }

    /**
     * Stores the given cookie.
     */
    fun put(cookie: Cookie) {
        val bucket = buckets.computeIfAbsent(cookie.getDomain()) { LinkedHashMap(20) }
        if (cookie.hasExpired()) {
            if (bucket.remove(cookie) != null) {
                totalCount--
            }
        } else {
            if (bucket.put(cookie, cookie) == null) {
                totalCount++
                if (bucket.size > MAX_BUCKET_SIZE) {
                    purge(bucket)
                }
                if (totalCount > TOTAL_COUNT_UPPER_THRESHOLD) {
                    purge()
                }
            }
        }
    }

    /**
     * Removes excess cookies from a given bucket.
     */
    private fun purge(bucket: MutableMap<Cookie, Cookie>) {
        var earliestCookie: Cookie? = null
        val it = bucket.values.iterator()
        while (it.hasNext()) {
            val cookie = it.next()
            if (cookie.hasExpired()) {
                it.remove()
                totalCount--
            } else {
                if (earliestCookie == null || cookie.lastAccessTime < earliestCookie.lastAccessTime) {
                    earliestCookie = cookie
                }
            }
        }
        if (bucket.size > MAX_BUCKET_SIZE) {
            bucket.remove(earliestCookie)
            totalCount--
        }
    }

    /**
     * Removes excess cookies globally.
     */
    private fun purge() {
        val removalQueue = PriorityQueue(totalCount / 2,
                CookieStore.RemovalComparator())

        for ((_, bucket) in buckets) {
            val it = bucket.values.iterator()
            while (it.hasNext()) {
                val cookie = it.next()
                if (cookie.hasExpired()) {
                    it.remove()
                    totalCount--
                } else {
                    removalQueue.add(cookie)
                }
            }
        }

        while (totalCount > TOTAL_COUNT_LOWER_THRESHOLD) {
            val cookie = removalQueue.remove()
            val bucket = buckets[cookie.getDomain()]
            if (bucket != null) {
                bucket.remove(cookie)
                totalCount--
            }
        }
    }

    private class GetComparator : Comparator<Cookie> {
        override fun compare(c1: Cookie, c2: Cookie): Int {
            val path1 = c1.path
            val path2 = c2.path

            val d = Integer.compare(path1?.length ?: 0, path2?.length ?: 0)
            return if (d != 0) {
                d
            } else c1.getCreationTime().compareTo(c2.getCreationTime())
        }
    }

    private class RemovalComparator : Comparator<Cookie> {
        override fun compare(c1: Cookie, c2: Cookie): Int {
            return (c1.lastAccessTime - c2.lastAccessTime).toInt()
        }
    }

    companion object {
        private const val MAX_BUCKET_SIZE = 50
        private const val TOTAL_COUNT_LOWER_THRESHOLD = 3000
        private const val TOTAL_COUNT_UPPER_THRESHOLD = 4000
    }
}
