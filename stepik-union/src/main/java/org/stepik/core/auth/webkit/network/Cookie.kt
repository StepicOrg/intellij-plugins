package org.stepik.core.auth.webkit.network

import java.net.URI
import java.text.ParseException
import java.util.regex.Pattern

/**
 * An RFC 6265-compliant cookie.
 */
internal class Cookie
/**
 * Creates a new `Cookie`.
 */
private constructor(
        /**
         * Returns the name of this cookie.
         */
        val name: String,
        /**
         * Returns the value of this cookie.
         */
        val value: String,
        /**
         * Returns the expiry time of this cookie.
         */
        val expiryTime: Long, private var domain: String?,
        /**
         * Returns the path of this cookie.
         */
        /**
         * Sets the path of this cookie.
         */
        var path: String?, private var creationTime: ExtendedTime?,
        /**
         * Returns the last access time of this cookie.
         */
        /**
         * Sets the last access time of this cookie.
         */
        var lastAccessTime: Long,
        /**
         * Returns the persistent property of this cookie.
         */
        val persistent: Boolean,
        /**
         * Returns the host-only property of this cookie.
         */
        /**
         * Sets the host-only property of this cookie.
         */
        var hostOnly: Boolean,
        /**
         * Returns the secure-only property of this cookie.
         */
        val secureOnly: Boolean,
        /**
         * Returns the http-only property of this cookie.
         */
        val httpOnly: Boolean) {

    /**
     * Returns the domain of this cookie.
     */
    fun getDomain(): String {
        if (domain == null) {
            domain = ""
        }
        return domain!!
    }

    /**
     * Sets the domain of this cookie.
     */
    fun setDomain(domain: String?) {
        this.domain = domain
    }

    /**
     * Returns the creation time of this cookie.
     */
    fun getCreationTime(): ExtendedTime {
        if (creationTime == null) {
            creationTime = ExtendedTime.currentTime()
        }
        return creationTime!!
    }

    /**
     * Sets the creation time of this cookie.
     */
    fun setCreationTime(creationTime: ExtendedTime?) {
        this.creationTime = creationTime
    }

    /**
     * Determines if this cookie has expired.
     */
    fun hasExpired(): Boolean {
        return System.currentTimeMillis() > expiryTime
    }

    /**
     * {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        return if (other is Cookie) {
            (equal(name, other.name)
                    && equal(domain, other.domain)
                    && equal(path, other.path))
        } else {
            false
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun hashCode(): Int {
        var hashCode = 7
        hashCode = 53 * hashCode + hashCode(name)
        hashCode = 53 * hashCode + hashCode(domain)
        hashCode = 53 * hashCode + hashCode(path)
        return hashCode
    }

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        return ("[name=" + name + ", value=" + value + ", "
                + "expiryTime=" + expiryTime + ", domain=" + domain + ", "
                + "path=" + path + ", creationTime=" + creationTime + ", "
                + "lastAccessTime=" + lastAccessTime + ", "
                + "persistent=" + persistent + ", hostOnly=" + hostOnly + ", "
                + "secureOnly=" + secureOnly + ", httpOnly=" + httpOnly + "]")
    }

    companion object {
        private val IP_ADDRESS_PATTERN = Pattern.compile(
                "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})")


        /**
         * Parses a `Set-Cookie` header string into a `Cookie`
         * object.
         */
        fun parse(setCookieString: String, currentTime: ExtendedTime): Cookie? {
            val items = setCookieString.split(";".toRegex()).toTypedArray()

            val nameValuePair = items[0].split("=".toRegex(), 2).toTypedArray()
            if (nameValuePair.size != 2) {
                return null
            }
            val name = nameValuePair[0].trim { it <= ' ' }
            val value = nameValuePair[1].trim { it <= ' ' }
            if (name.isEmpty()) {
                return null
            }

            var expires: Long? = null
            var maxAge: Long? = null
            var domain: String? = null
            var path: String? = null
            var secure = false
            var httpOnly = false

            for (i in 1 until items.size) {
                val terms = items[i].split("=".toRegex(), 2).toTypedArray()
                val attrName = terms[0].trim { it <= ' ' }
                val attrValue = (if (terms.size > 1) terms[1] else "").trim { it <= ' ' }

                try {
                    when {
                        "Expires".equals(attrName, ignoreCase = true) -> expires = parseExpires(attrValue)
                        "Max-Age".equals(attrName, ignoreCase = true) -> maxAge = parseMaxAge(attrValue, currentTime.baseTime())
                        "Domain".equals(attrName, ignoreCase = true) -> domain = parseDomain(attrValue)
                        "Path".equals(attrName, ignoreCase = true) -> path = parsePath(attrValue)
                        "Secure".equals(attrName, ignoreCase = true) -> secure = true
                        "HttpOnly".equals(attrName, ignoreCase = true) -> httpOnly = true
                    }
                } catch (ignored: ParseException) {
                }

            }

            val expiryTime: Long
            val persistent: Boolean
            when {
                maxAge != null -> {
                    persistent = true
                    expiryTime = maxAge
                }
                expires != null -> {
                    persistent = true
                    expiryTime = expires
                }
                else -> {
                    persistent = false
                    expiryTime = java.lang.Long.MAX_VALUE
                }
            }

            if (domain == null) {
                domain = ""
            }

            return Cookie(name, value, expiryTime, domain, path,
                    currentTime, currentTime.baseTime(), persistent, false,
                    secure, httpOnly)
        }

        /**
         * Parses the value of the `Expires` attribute.
         */
        @Throws(ParseException::class)
        private fun parseExpires(attributeValue: String): Long {
            try {
                return Math.max(DateParser.parse(attributeValue), 0)
            } catch (ex: ParseException) {
                throw ParseException("Error parsing Expires attribute", 0)
            }

        }

        /**
         * Parses the value of the `Max-Age` attribute.
         */
        @Throws(ParseException::class)
        private fun parseMaxAge(attributeValue: String, currentTime: Long): Long {
            try {
                val maxAge = java.lang.Long.parseLong(attributeValue)
                return if (maxAge <= 0) {
                    0
                } else {
                    if (maxAge > (java.lang.Long.MAX_VALUE - currentTime) / 1000)
                        java.lang.Long.MAX_VALUE
                    else
                        currentTime + maxAge * 1000
                }
            } catch (ex: NumberFormatException) {
                throw ParseException("Error parsing Max-Age attribute", 0)
            }

        }

        /**
         * Parses the value of the `Domain` attribute.
         */
        @Throws(ParseException::class)
        private fun parseDomain(attributeValue: String): String {
            var myAttributeValue = attributeValue
            if (myAttributeValue.isEmpty()) {
                throw ParseException("Domain attribute is empty", 0)
            }
            if (myAttributeValue.startsWith(".")) {
                myAttributeValue = myAttributeValue.substring(1)
            }
            return myAttributeValue.toLowerCase()
        }

        /**
         * Parses the value of the `Path` attribute.
         */
        private fun parsePath(attributeValue: String): String? {
            return if (attributeValue.startsWith("/")) attributeValue else null
        }

        /**
         * Determines, in null-safe manner, if two objects are equal.
         */
        private fun equal(obj1: Any?, obj2: Any?): Boolean {
            return obj1 == null && obj2 == null || obj1 != null && obj1 == obj2
        }

        /**
         * Computes the hash code of an object in null safe-manner.
         */
        private fun hashCode(obj: Any?): Int {
            return obj?.hashCode() ?: 0
        }

        /**
         * Determines if a domain matches another domain.
         */
        fun domainNotMatches(domain: String, cookieDomain: String): Boolean {
            return !domain.endsWith(cookieDomain) || domain.length != cookieDomain.length && (domain[domain.length
                    - cookieDomain.length - 1] != '.' || isIpAddress(domain))
        }

        /**
         * Determines if a hostname is an IP address.
         */
        private fun isIpAddress(hostname: String): Boolean {
            val matcher = IP_ADDRESS_PATTERN.matcher(hostname)
            if (!matcher.matches()) {
                return false
            }
            return (1..matcher.groupCount()).none { matcher.group(it).toInt() > 255 }
        }

        /**
         * Computes the default path for a given URI.
         */
        fun defaultPath(uri: URI): String {
            var path: String? = uri.path
            if (path == null || !path.startsWith("/")) {
                return "/"
            }
            path = path.substring(0, path.lastIndexOf("/"))
            return if (path.isEmpty()) {
                "/"
            } else path
        }

        /**
         * Determines if a path matches another path.
         */
        fun pathMatches(path: String?, cookiePath: String?): Boolean {
            return path != null && cookiePath != null && path.startsWith(cookiePath) && (path.length == cookiePath.length
                    || cookiePath.endsWith("/")
                    || path[cookiePath.length] == '/')
        }
    }
}
