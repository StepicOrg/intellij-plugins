package org.stepik.core.auth.webkit.network

import sun.security.util.SecurityConstants
import java.net.CookieHandler
import java.net.URI
import java.util.*

/**
 * An RFC 6265-compliant cookie handler.
 */
/**
 * Creates a new `CookieManager`.
 */
class CookieManager : CookieHandler() {
    private val store = CookieStore()

    /**
     * Canonicalize a hostname as required by RFC 6265.
     */
    private fun canonicalize(hostname: String): String {
        // The hostname is already all-ASCII at this point
        return hostname.toLowerCase()
    }

    /**
     * {@inheritDoc}
     */
    override fun get(uri: URI?, requestHeaders: Map<String, List<String>>?): Map<String, List<String>> {
        if (uri == null) {
            throw IllegalArgumentException("uri is null")
        }
        if (requestHeaders == null) {
            throw IllegalArgumentException("requestHeaders is null")
        }

        val cookieString = get(uri)

        val result: Map<String, List<String>>
        if (cookieString != null) {
            result = HashMap()
            result["Cookie"] = listOf(cookieString)
        } else {
            result = emptyMap()
        }

        return result
    }

    /**
     * Returns the cookie string for a given URI.
     */
    private operator fun get(uri: URI): String? {
        var host: String? = uri.host
        if (host?.isEmpty() != false) {
            return null
        }
        host = canonicalize(host)

        val scheme = uri.scheme
        val secureProtocol = "https".equals(scheme, ignoreCase = true) || "javascripts".equals(scheme, ignoreCase = true)
        val httpApi = "http".equals(scheme, ignoreCase = true) || "https".equals(scheme, ignoreCase = true)

        var cookieList: List<Cookie>? = null
        synchronized(store) {
            cookieList = store.get(host!!, uri.path,
                    secureProtocol, httpApi)
        }

        val sb = StringBuilder()
        for (cookie in cookieList!!) {
            if (sb.isNotEmpty()) {
                sb.append("; ")
            }
            sb.append(cookie.name)
            sb.append('=')
            sb.append(cookie.value)
        }

        return if (sb.isNotEmpty()) sb.toString() else null
    }

    /**
     * {@inheritDoc}
     */
    override fun put(uri: URI?, responseHeaders: Map<String, List<String>>?) {
        if (uri == null) {
            throw IllegalArgumentException("uri is null")
        }
        if (responseHeaders == null) {
            throw IllegalArgumentException("responseHeaders is null")
        }

        for ((key, value) in responseHeaders) {
            if (!"Set-Cookie".equals(key, ignoreCase = true)) {
                continue
            }
            var currentTime = ExtendedTime.currentTime()
            // RT-15907: Process the list of headers in reverse order,
            // effectively restoring the order in which the headers were
            // received from the server. This is a temporary workaround for
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7059532
            val it = value.listIterator(value.size)
            while (it.hasPrevious()) {
                val cookie = Cookie.parse(it.previous(), currentTime)
                if (cookie != null) {
                    put(uri, cookie)
                    currentTime = currentTime.incrementSubtime()
                }
            }
        }
    }

    /**
     * Puts an individual cookie.
     */
    private fun put(uri: URI, cookie: Cookie) {
        var host: String? = uri.host
        if (host?.isEmpty() != false) {
            return
        }
        host = canonicalize(host)

        if (PublicSuffixes.isPublicSuffix(cookie.getDomain())) {
            if (host == cookie.getDomain()) {
                cookie.setDomain("")
            } else {
                return
            }
        }

        if (cookie.getDomain().isNotEmpty()) {
            if (Cookie.domainNotMatches(host, cookie.getDomain())) {
                return
            } else {
                cookie.hostOnly = false
            }
        } else {
            cookie.hostOnly = true
            cookie.setDomain(host)
        }

        if (cookie.path == null) {
            cookie.path = Cookie.defaultPath(uri)
        }

        val httpApi = "http".equals(uri.scheme, ignoreCase = true) || "https".equals(uri.scheme, ignoreCase = true)
        if (cookie.httpOnly && !httpApi) {
            return
        }

        synchronized(store) {
            val oldCookie = store.get(cookie)
            if (oldCookie != null) {
                if (oldCookie.httpOnly && !httpApi) {
                    return
                }
                cookie.setCreationTime(oldCookie.getCreationTime())
            }

            store.put(cookie)
        }
    }

    fun clear() {
        store.removeAll()
    }

    fun save() {
        store.save()
    }

    companion object {
        private var cookieHandler: CookieHandler? = null

        @Synchronized
        fun getDefault(): CookieHandler? {
            val var0 = System.getSecurityManager()
            var0?.checkPermission(SecurityConstants.GET_COOKIEHANDLER_PERMISSION)

            return cookieHandler
        }

        @Synchronized
        fun setDefault(var0: CookieHandler) {
            val var1 = System.getSecurityManager()
            var1?.checkPermission(SecurityConstants.SET_COOKIEHANDLER_PERMISSION)

            cookieHandler = var0
        }
    }
}
