package org.stepik.core.serialization

import com.thoughtworks.xstream.converters.SingleValueConverter
import org.stepik.api.objects.steps.Sample

class SampleConverter : SingleValueConverter {

    override fun toString(sample: Any): String {
        sample as Sample

        return "<input>${sample.input}</input><output>${sample.output}</output>"
    }

    override fun fromString(s: String): Any {
        val sample = Sample()

        val matcher = pattern.matchEntire(s) ?: return sample

        sample.input = matcher.groupValues[1]
        sample.output = matcher.groupValues[2]

        return sample
    }

    override fun canConvert(aClass: Class<*>): Boolean {
        return aClass == Sample::class.java
    }

    companion object {
        private val pattern = "<input>([^<>]*)</input><output>([^<>]*)</output>".toRegex()
    }
}
