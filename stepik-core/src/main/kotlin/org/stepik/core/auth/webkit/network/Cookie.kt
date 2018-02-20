package org.stepik.core.auth.webkit.network

import java.net.URI
import java.util.regex.Pattern

/**
 * An RFC 6265-compliant cookie.
 */
internal class Cookie private constructor(
        val name: String,
        val value: String,
        val expiryTime: Long,
        private var domain: String?,
        var path: String?,
        private var creationTime: ExtendedTime?,
        var lastAccessTime: Long,
        val persistent: Boolean,
        var hostOnly: Boolean,
        val secureOnly: Boolean,
        val httpOnly: Boolean) {

    fun getDomain(): String {
        if (domain == null) {
            domain = ""
        }
        return domain!!
    }

    fun setDomain(domain: String?) {
        this.domain = domain
    }

    fun getCreationTime(): ExtendedTime {
        if (creationTime == null) {
            creationTime = ExtendedTime.currentTime()
        }
        return creationTime!!
    }

    fun setCreationTime(creationTime: ExtendedTime?) {
        this.creationTime = creationTime
    }

    fun hasExpired(): Boolean {
        return System.currentTimeMillis() > expiryTime
    }

    override fun equals(other: Any?): Boolean {
        return other is Cookie &&
                name == other.name && domain == other.domain && path == other.path
    }

    override fun hashCode(): Int {
        var hashCode = 7
        hashCode = 53 * hashCode + hashCode(name)
        hashCode = 53 * hashCode + hashCode(domain)
        hashCode = 53 * hashCode + hashCode(path)
        return hashCode
    }

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

        fun parse(setCookieString: String, currentTime: ExtendedTime): Cookie? {
            val items = setCookieString.split(";")

            val nameValuePair = items.first().split("=", limit = 2)
            if (nameValuePair.size != 2) {
                return null
            }
            val name = nameValuePair[0].trim()
            val value = nameValuePair[1].trim()
            if (name.isEmpty()) {
                return null
            }

            val values = items.associate {
                val terms = it.split("=", limit = 2)
                        .map { it.trim() }
                val attrName = terms.first().toLowerCase()
                val attrValue = terms.getOrElse(1) { "" }
                attrName to attrValue
            }

            val expires: Long? = parseExpires(values["expires"])
            val maxAge: Long? = parseMaxAge(values["max-age"], currentTime.baseTime())
            val domain: String = parseDomain(values["domain"])
            val path: String? = parsePath(values["path"])
            val secure = "secure" in values.keys
            val httpOnly = "httponly" in values.keys
            val expiryTime: Long = maxAge ?: expires ?: Long.MAX_VALUE
            val persistent = maxAge != null || expires != null

            return Cookie(name, value, expiryTime, domain, path,
                    currentTime, currentTime.baseTime(), persistent, false,
                    secure, httpOnly)
        }

        private fun parseExpires(attributeValue: String?): Long? {
            attributeValue ?: return null
            val expires = DateParser.parse(attributeValue) ?: return null
            return Math.max(expires, 0)
        }

        private fun parseMaxAge(attributeValue: String?, currentTime: Long): Long? {
            val maxAge = attributeValue?.toLongOrNull() ?: return null
            return if (maxAge <= 0) {
                0
            } else {
                if (maxAge > (Long.MAX_VALUE - currentTime) / 1000)
                    Long.MAX_VALUE
                else
                    currentTime + maxAge * 1000
            }
        }

        private fun parseDomain(attributeValue: String?): String {
            attributeValue ?: return ""

            return (if (attributeValue.startsWith(".")) {
                attributeValue.substring(1)
            } else attributeValue).toLowerCase()
        }

        private fun parsePath(attributeValue: String?): String? {
            return if (attributeValue?.startsWith("/") == true) attributeValue else null
        }

        private fun hashCode(obj: Any?): Int {
            return obj?.hashCode() ?: 0
        }

        fun domainNotMatches(domain: String, cookieDomain: String): Boolean {
            return !domain.endsWith(cookieDomain) || domain.length != cookieDomain.length && (domain[domain.length
                    - cookieDomain.length - 1] != '.' || isIpAddress(domain))
        }

        private fun isIpAddress(hostname: String): Boolean {
            val matcher = IP_ADDRESS_PATTERN.matcher(hostname)
            if (!matcher.matches()) {
                return false
            }
            return (1..matcher.groupCount()).none { matcher.group(it).toInt() > 255 }
        }

        fun defaultPath(uri: URI): String {
            var path: String? = uri.path
            if (path?.startsWith("/") != true) {
                return "/"
            }
            path = path.substring(0, path.lastIndexOf("/"))
            return if (path.isEmpty()) {
                "/"
            } else path
        }

        fun pathMatches(path: String?, cookiePath: String?): Boolean {
            return path != null && cookiePath != null
                    && path.startsWith(cookiePath)
                    && (path.length == cookiePath.length
                    || cookiePath.endsWith("/")
                    || path[cookiePath.length] == '/')
        }
    }
}
