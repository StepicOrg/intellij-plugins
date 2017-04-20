package org.stepik.plugin.auth.webkit.network;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.IDN;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A collection of static utility methods dealing with "public suffixes".
 */
final class PublicSuffixes {
    private static final Logger logger = Logger.getInstance(PublicSuffixes.class);
    /**
     * The mapping from domain names to public suffix list rules.
     */
    private static final Map<String, PublicSuffixes.Rule> RULES = loadRules();

    /**
     * The private default constructor. Ensures non-instantiability.
     */
    private PublicSuffixes() {
        throw new AssertionError();
    }

    /**
     * Determines if a domain is a public suffix.
     */
    static boolean isPublicSuffix(@NotNull String domain) {
        if (domain.length() == 0) {
            return false;
        }
        PublicSuffixes.Rule rule = RULES.get(domain);
        if (rule == PublicSuffixes.Rule.EXCEPTION_RULE) {
            return false;
        } else if (rule == PublicSuffixes.Rule.SIMPLE_RULE || rule == PublicSuffixes.Rule.WILDCARD_RULE) {
            return true;
        } else {
            int pos = domain.indexOf('.') + 1;
            if (pos == 0) {
                pos = domain.length();
            }
            String parent = domain.substring(pos);
            return RULES.get(parent) == PublicSuffixes.Rule.WILDCARD_RULE;
        }
    }

    /**
     * Loads the public suffix list from a given resource.
     */
    @NotNull
    private static Map<String, PublicSuffixes.Rule> loadRules() {
        Map<String, PublicSuffixes.Rule> result = null;

        InputStream is = PublicSuffixes.class.getResourceAsStream("effective_tld_names.dat");
        if (is != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                result = loadRules(reader);
            } catch (IOException ex) {
                logger.warn("Unexpected error", ex);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ex) {
                    logger.warn("Unexpected error", ex);
                }
            }
        } else {
            logger.warn(String.format("Resource not found: [%s]", "effective_tld_names.dat"));
        }

        result = result != null
                ? Collections.unmodifiableMap(result)
                : Collections.emptyMap();
        return result;
    }

    /**
     * Loads the public suffix list from a given reader.
     */
    @NotNull
    private static Map<String, PublicSuffixes.Rule> loadRules(@NotNull BufferedReader reader)
            throws IOException {
        Map<String, PublicSuffixes.Rule> result = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.split("\\s+", 2)[0];
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith("//")) {
                continue;
            }
            PublicSuffixes.Rule rule;
            if (line.startsWith("!")) {
                line = line.substring(1);
                rule = PublicSuffixes.Rule.EXCEPTION_RULE;
            } else if (line.startsWith("*.")) {
                line = line.substring(2);
                rule = PublicSuffixes.Rule.WILDCARD_RULE;
            } else {
                rule = PublicSuffixes.Rule.SIMPLE_RULE;
            }
            try {
                line = IDN.toASCII(line, IDN.ALLOW_UNASSIGNED);
            } catch (Exception ex) {
                logger.warn(String.format("Error parsing rule: [%s]", line), ex);
                continue;
            }
            result.put(line, rule);
        }
        return result;
    }

    /**
     * Public suffix list rule types.
     */
    private enum Rule {
        SIMPLE_RULE,
        WILDCARD_RULE,
        EXCEPTION_RULE,
    }
}
