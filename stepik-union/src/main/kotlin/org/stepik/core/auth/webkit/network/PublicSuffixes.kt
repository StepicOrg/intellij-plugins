package org.stepik.core.auth.webkit.network

import org.stepik.core.common.Loggable
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.IDN
import java.util.*

/**
 * A collection of static utility methods dealing with "public suffixes".
 */
internal class PublicSuffixes
/**
 * The private default constructor. Ensures non-instantiability.
 */
private constructor() {

    init {
        throw AssertionError()
    }

    /**
     * Public suffix list rule types.
     */
    private enum class Rule {
        SIMPLE_RULE,
        WILDCARD_RULE,
        EXCEPTION_RULE
    }

    companion object : Loggable {
        /**
         * The mapping from domain names to public suffix list rules.
         */
        private val RULES = loadRules()

        /**
         * Determines if a domain is a public suffix.
         */
        fun isPublicSuffix(domain: String): Boolean {
            if (domain.isEmpty()) {
                return false
            }
            val rule = RULES[domain]
            return when (rule) {
                PublicSuffixes.Rule.EXCEPTION_RULE -> false
                PublicSuffixes.Rule.SIMPLE_RULE, PublicSuffixes.Rule.WILDCARD_RULE -> true
                else -> {
                    var pos = domain.indexOf('.') + 1
                    if (pos == 0) {
                        pos = domain.length
                    }
                    val parent = domain.substring(pos)
                    RULES[parent] == PublicSuffixes.Rule.WILDCARD_RULE
                }
            }
        }

        /**
         * Loads the public suffix list from a given resource.
         */
        private fun loadRules(): Map<String, PublicSuffixes.Rule> {
            var result: Map<String, PublicSuffixes.Rule>? = null

            val `is` = PublicSuffixes::class.java.getResourceAsStream("effective_tld_names.dat")
            if (`is` != null) {
                var reader: BufferedReader? = null
                try {
                    reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
                    result = loadRules(reader)
                } catch (ex: IOException) {
                    logger.warn("Unexpected error", ex)
                } finally {
                    try {
                        if (reader != null) {
                            reader.close()
                        }
                    } catch (ex: IOException) {
                        logger.warn("Unexpected error", ex)
                    }

                }
            } else {
                logger.warn(String.format("Resource not found: [%s]", "effective_tld_names.dat"))
            }

            result = if (result != null)
                Collections.unmodifiableMap(result)
            else
                emptyMap()
            return result!!
        }

        /**
         * Loads the public suffix list from a given reader.
         */
        @Throws(IOException::class)
        private fun loadRules(reader: BufferedReader): Map<String, PublicSuffixes.Rule> {
            val result = LinkedHashMap<String, PublicSuffixes.Rule>()
            var line: String
            while (true) {
                line = reader.readLine() ?: break
                line = line.split("\\s+".toRegex(), 2).toTypedArray()[0]
                if (line.isEmpty()) {
                    continue
                }
                if (line.startsWith("//")) {
                    continue
                }
                val rule: PublicSuffixes.Rule
                when {
                    line.startsWith("!") -> {
                        line = line.substring(1)
                        rule = PublicSuffixes.Rule.EXCEPTION_RULE
                    }
                    line.startsWith("*.") -> {
                        line = line.substring(2)
                        rule = PublicSuffixes.Rule.WILDCARD_RULE
                    }
                    else -> rule = PublicSuffixes.Rule.SIMPLE_RULE
                }
                try {
                    line = IDN.toASCII(line, IDN.ALLOW_UNASSIGNED)
                } catch (ex: Exception) {
                    logger.warn(String.format("Error parsing rule: [%s]", line), ex)
                    continue
                }

                result[line] = rule
            }
            return result
        }
    }
}
