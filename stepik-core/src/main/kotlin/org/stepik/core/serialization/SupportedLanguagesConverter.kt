package org.stepik.core.serialization

import com.thoughtworks.xstream.converters.SingleValueConverter
import org.stepik.core.SupportedLanguages
import org.stepik.core.SupportedLanguages.INVALID

class SupportedLanguagesConverter : SingleValueConverter {
    override fun toString(any: Any): String {
        return (any as SupportedLanguages).title
    }

    override fun fromString(str: String): SupportedLanguages {
        val language = SupportedLanguages.langOfName(str)
        if (language != INVALID) {
            return language
        }

        if (str == "Java") {
            return SupportedLanguages.JAVA7
        }

        return SupportedLanguages.langOfTitle(str)
    }

    override fun canConvert(clazz: Class<*>): Boolean {
        return clazz == SupportedLanguages::class.java
    }
}
