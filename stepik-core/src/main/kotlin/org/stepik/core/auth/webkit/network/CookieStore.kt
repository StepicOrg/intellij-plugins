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

    private val bucketsType = object : TypeToken<Map<String, Set<Cookie>>>() {}.type
    private val gson = GsonBuilder().create()

    /**
     * The mapping from domain names to cookie buckets.
     * Each cookie bucket stores the cookies associated with the
     * corresponding domain. Each cookie bucket is represented
     * by a Map<Cookie></Cookie>,Cookie> to facilitate retrieval of a cookie
     * by another cookie with the same name, domain, and path.
     */
    private val buckets: MutableMap<String, MutableMap<Cookie, Cookie>> = loadCookies()
    private var totalCount = buckets.map { it.value.size }.sum()

    private fun loadCookies(): MutableMap<String, MutableMap<Cookie, Cookie>> {
        val attributes = CredentialAttributes(this.javaClass.name, "cookies", javaClass, false)
        val credentials = PasswordSafe.getInstance()[attributes]
        val json = credentials?.getPasswordAsString()

        if (json != null) {
            try {
                val cookies: Map<String, Set<Cookie>> = gson.fromJson(json, bucketsType)
                return mutableMapOf(*cookies.map {
                    it.key to mutableMapOf(*it.value.map { it to it }.toTypedArray())
                }.toTypedArray())
            } catch (e: JsonSyntaxException) {
                logger.warn(e)
            }
        }
        return mutableMapOf()
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
        var cookies: List<Cookie>? = null

        var domain = hostname
        while (domain.isNotEmpty()) {
            val bucket = buckets[domain]
            if (bucket != null) {
                cookies = find(bucket, hostname, path, secureProtocol, httpApi)
            }
            val nextPoint = domain.indexOf('.')
            if (nextPoint != -1) {
                domain = domain.substring(nextPoint + 1)
            } else {
                break
            }
        }

        cookies = cookies?.sortedWith(GetComparator) ?: return emptyList()

        val currentTime = System.currentTimeMillis()
        for (cookie in cookies) {
            cookie.lastAccessTime = currentTime
        }

        return cookies
    }

    /**
     * Finds all the cookies that are stored in the given bucket and
     * match the given query.
     */
    private fun find(
            bucket: MutableMap<Cookie, Cookie>,
            hostname: String,
            path: String?,
            secureProtocol: Boolean,
            httpApi: Boolean): List<Cookie> {
        val it = bucket.values.iterator()
        val result = mutableListOf<Cookie>()
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
            } else if (Cookie.domainNotMatches(hostname, cookie.getDomain())) {
                continue
            }

            if (Cookie.pathMatches(path, cookie.path)
                    && !(cookie.secureOnly && !secureProtocol)
                    && !(cookie.httpOnly && !httpApi)) {
                result.add(cookie)
            }
        }
        return result
    }

    fun removeAll() {
        totalCount = 0
        buckets.clear()
    }

    fun save() {
        val attributes = CredentialAttributes(javaClass.name, "cookies", javaClass, false)
        val cookies = buckets.map { it.key to it.value.values.toSet() }.toMap()
        val serialized = gson.toJson(cookies, bucketsType)
        val credentials = Credentials(attributes.userName, serialized)
        PasswordSafe.getInstance().set(attributes, credentials)
    }

    /**
     * Stores the given cookie.
     */
    fun put(cookie: Cookie) {
        val bucket = buckets.computeIfAbsent(cookie.getDomain()) { mutableMapOf() }
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
            } else if (earliestCookie == null
                    || cookie.lastAccessTime < earliestCookie.lastAccessTime) {
                earliestCookie = cookie
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
        val removalQueue = PriorityQueue(totalCount / 2, RemovalComparator)

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

    private object GetComparator : Comparator<Cookie> {
        override fun compare(c1: Cookie, c2: Cookie): Int {
            val d = (c1.path?.length ?: 0).compareTo(c2.path?.length ?: 0)
            return if (d != 0) {
                d
            } else c1.getCreationTime().compareTo(c2.getCreationTime())
        }
    }

    private object RemovalComparator : Comparator<Cookie> {
        override fun compare(c1: Cookie, c2: Cookie): Int {
            return c1.lastAccessTime.compareTo(c2.lastAccessTime)
        }
    }

    companion object {
        private const val MAX_BUCKET_SIZE = 50
        private const val TOTAL_COUNT_LOWER_THRESHOLD = 3000
        private const val TOTAL_COUNT_UPPER_THRESHOLD = 4000
    }
}
