package org.stepik.core.serialization

import org.stepik.core.SupportedLanguages
import com.thoughtworks.xstream.converters.SingleValueConverter

import org.stepik.core.SupportedLanguages.INVALID

class SupportedLanguagesConverter : SingleValueConverter {
    override fun toString(o: Any): String {
        return (o as SupportedLanguages).title
    }

    override fun fromString(s: String): Any {
        val language = SupportedLanguages.langOfName(s)
        if (language != INVALID) {
            return language
        }

        return SupportedLanguages.langOfTitle(s)
    }

    override fun canConvert(aClass: Class<*>): Boolean {
        return aClass == SupportedLanguages::class.java
    }
}