package org.stepik.core.serialization;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.intellij.lang.annotations.Language;
import org.stepik.api.objects.steps.Sample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleConverter implements SingleValueConverter {
    @Language("HTML")
    private final static String TEMPLATE = "<input>%s</input><output>%s</output>";
    @Language("REGEXP")
    private final static String PATTERN_INPUT = "<input>([^<>]*)</input><output>([^<>]*)</output>";
    private final static Pattern pattern = Pattern.compile(PATTERN_INPUT);

    @Override
    public String toString(Object o) {
        Sample sample = (Sample) o;

        return String.format(TEMPLATE, sample.getInput(), sample.getOutput());
    }

    @Override
    public Object fromString(String s) {
        Sample sample = new Sample();

        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            sample.setInput(matcher.group(1));
            sample.setOutput(matcher.group(2));
        }
        return sample;
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass == Sample.class;
    }
}