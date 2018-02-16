package org.stepik.core.serialization

import com.thoughtworks.xstream.converters.SingleValueConverter
import org.intellij.lang.annotations.Language
import org.stepik.api.objects.steps.Sample
import java.util.regex.Pattern

class SampleConverter : SingleValueConverter {

    override fun toString(o: Any): String {
        val sample = o as Sample

        return String.format(TEMPLATE, sample.input, sample.output)
    }

    override fun fromString(s: String): Any {
        val sample = Sample()

        val matcher = pattern.matcher(s)
        if (matcher.matches()) {
            sample.setInput(matcher.group(1))
            sample.setOutput(matcher.group(2))
        }
        return sample
    }

    override fun canConvert(aClass: Class<*>): Boolean {
        return aClass == Sample::class.java
    }

    companion object {
        @Language("HTML")
        private val TEMPLATE = "<input>%s</input><output>%s</output>"
        @Language("REGEXP")
        private val PATTERN_INPUT = "<input>([^<>]*)</input><output>([^<>]*)</output>"
        private val pattern = Pattern.compile(PATTERN_INPUT)
    }
}
