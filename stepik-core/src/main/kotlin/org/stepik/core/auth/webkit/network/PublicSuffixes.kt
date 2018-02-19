package org.stepik.core.auth.webkit.network

import org.stepik.core.common.Loggable
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.IDN

/**
 * A collection of static utility methods dealing with "public suffixes".
 */
internal object PublicSuffixes : Loggable {

    /**
     * Public suffix list rule types.
     */
    private enum class Rule {
        SIMPLE_RULE,
        WILDCARD_RULE,
        EXCEPTION_RULE
    }

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
            Rule.EXCEPTION_RULE -> false
            Rule.SIMPLE_RULE, Rule.WILDCARD_RULE -> true
            null -> {
                var pos = domain.indexOf('.') + 1
                if (pos == 0) {
                    pos = domain.length
                }
                val parent = domain.substring(pos)
                RULES[parent] == Rule.WILDCARD_RULE
            }
        }
    }

    /**
     * Loads the public suffix list from a given resource.
     */
    private fun loadRules(): Map<String, Rule> {
        val effectiveTldNames = javaClass.getResourceAsStream("effective_tld_names.dat")
        if (effectiveTldNames != null) {
            try {
                BufferedReader(InputStreamReader(effectiveTldNames, "UTF-8")).use {
                    return loadRules(it)
                }
            } catch (ex: IOException) {
                logger.warn("Unexpected error", ex)
            }
        } else {
            logger.warn(String.format("Resource not found: [%s]", "effective_tld_names.dat"))
        }

        return emptyMap()
    }

    /**
     * Loads the public suffix list from a given reader.
     */
    private fun loadRules(reader: BufferedReader): Map<String, Rule> {
        val result = mutableMapOf<String, Rule>()
        var line: String
        while (true) {
            line = reader.readLine() ?: break
            line = line.split("\\s+".toRegex(), 2).first()
            if (line.isEmpty() || line.startsWith("//")) {
                continue
            }
            val rule: Rule
            when {
                line.startsWith("!") -> {
                    line = line.substring(1)
                    rule = Rule.EXCEPTION_RULE
                }
                line.startsWith("*.") -> {
                    line = line.substring(2)
                    rule = Rule.WILDCARD_RULE
                }
                else -> rule = Rule.SIMPLE_RULE
            }

            try {
                line = IDN.toASCII(line, IDN.ALLOW_UNASSIGNED)
                result[line] = rule
            } catch (ex: Exception) {
                logger.warn("Error parsing rule: [$line]", ex)
            }
        }
        return result
    }
}
