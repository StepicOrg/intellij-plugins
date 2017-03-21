package org.stepik.core.serialization;

import org.stepik.core.SupportedLanguages;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import static org.stepik.core.SupportedLanguages.INVALID;

public class SupportedLanguagesConverter implements SingleValueConverter {
    @Override
    public String toString(Object o) {
        return ((SupportedLanguages) o).getTitle();
    }

    @Override
    public Object fromString(String s) {
        SupportedLanguages language = SupportedLanguages.langOfName(s);
        if (language != INVALID) {
            return language;
        }

        return SupportedLanguages.langOfTitle(s);
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass == SupportedLanguages.class;
    }
}