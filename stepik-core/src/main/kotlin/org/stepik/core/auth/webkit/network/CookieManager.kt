package org.stepik.core.auth.webkit.network

import sun.security.util.SecurityConstants
import java.net.CookieHandler
import java.net.URI

/**
 * An RFC 6265-compliant cookie handler.
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

    override fun get(uri: URI?, requestHeaders: Map<String, List<String>>?): Map<String, List<String>> {
        uri ?: throw IllegalArgumentException("uri is null")
        requestHeaders ?: throw IllegalArgumentException("requestHeaders is null")

        val cookieString = get(uri)

        return if (cookieString != null) {
            mapOf("Cookie" to listOf(cookieString))
        } else {
            emptyMap()
        }
    }

    /**
     * Returns the cookie string for a given URI.
     */
    private operator fun get(uri: URI): String? {
        val host: String? = uri.host
        if (host?.isEmpty() != false) {
            return null
        }

        val scheme = uri.scheme.toLowerCase()
        val secureProtocol = scheme == "https" || scheme == "javascripts"
        val httpApi = scheme == "http" || scheme == "https"

        var cookieList: List<Cookie>? = null
        synchronized(store) {
            cookieList = store[canonicalize(host), uri.path, secureProtocol, httpApi]
        }

        val cookies = cookieList!!.joinToString("; ") { "${it.name}=${it.value}" }

        return if (cookies.isNotEmpty()) cookies else null
    }

    override fun put(uri: URI?, responseHeaders: Map<String?, List<String>>?) {
        uri ?: throw IllegalArgumentException("uri is null")
        responseHeaders ?: throw IllegalArgumentException("responseHeaders is null")

        val cookies = responseHeaders.filter { it.key?.toLowerCase() == "set-cookie" }
                .map { it.value }

        for (values in cookies) {
            var currentTime = ExtendedTime.currentTime()
            // RT-15907: Process the list of headers in reverse order,
            // effectively restoring the order in which the headers were
            // received from the server. This is a temporary workaround for
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7059532
            for (value in values.reversed()) {
                val cookie = Cookie.parse(value, currentTime)
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

        val scheme = uri.scheme.toLowerCase()
        val httpApi = scheme == "http" || scheme == "https"
        if (cookie.httpOnly && !httpApi) {
            return
        }

        synchronized(store) {
            val oldCookie = store[cookie]
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
            val securityManager = System.getSecurityManager()
            securityManager?.checkPermission(SecurityConstants.GET_COOKIEHANDLER_PERMISSION)

            return cookieHandler
        }

        @Synchronized
        fun setDefault(handler: CookieHandler) {
            val securityManager = System.getSecurityManager()
            securityManager?.checkPermission(SecurityConstants.SET_COOKIEHANDLER_PERMISSION)

            cookieHandler = handler
        }
    }
}
