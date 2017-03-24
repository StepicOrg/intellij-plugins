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
    private final static String PATTERN_INPUT = "<input>([^<>]*)</input>";
    @Language("REGEXP")
    private final static String PATTERN_OUTPUT = "<input>([^<>]*)</input>";
    private final static Pattern pattern_input = Pattern.compile(PATTERN_INPUT);
    private final static Pattern pattern_output = Pattern.compile(PATTERN_OUTPUT);

    @Override
    public String toString(Object o) {
        Sample sample = (Sample) o;

        return String.format(TEMPLATE, sample.getInput(), sample.getOutput());
    }

    @Override
    public Object fromString(String s) {
        Sample sample = new Sample();

        Matcher matcher = pattern_input.matcher(s);
        if (matcher.matches()) {
            sample.setInput(matcher.group(1));
        }

        matcher = pattern_output.matcher(s);
        if (matcher.matches()) {
            sample.setOutput(matcher.group(1));
        }
        return sample;
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass == Sample.class;
    }
}