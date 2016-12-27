package com.jetbrains.tmp.learning;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.intellij.openapi.diagnostic.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class StudySerializationUtils {

    private StudySerializationUtils() {
    }

    static class StudyUnrecognizedFormatException extends Exception {
        StudyUnrecognizedFormatException(String message) {
            super(message);
        }
    }

    static class Xml {
        final static String MAIN_ELEMENT = "StepikProjectManager";
        private static final Logger logger = Logger.getInstance(StudySerializationUtils.class);
        private static final String VALUE = "value";
        private static final String NAME = "name";
        private static final String VERSION = "version";

        private Xml() {
        }

        static int getVersion(Element element) throws StudyUnrecognizedFormatException {
            final Element stepManager = element.getChild(MAIN_ELEMENT);
            if (stepManager == null) {
                String message = "Can't get a version: not found element \"" + MAIN_ELEMENT + "\"";
                throw new StudyUnrecognizedFormatException(message);
            }
            Element versionElement = getChildWithName(stepManager, VERSION);
            try {
                return Integer.valueOf(versionElement.getAttributeValue(VALUE));
            } catch (NumberFormatException e) {
                throw new StudyUnrecognizedFormatException("Can't get a version: " + versionElement.toString());
            }
        }

        @NotNull
        static Element getChildWithName(@NotNull Element parent, @NotNull String name)
                throws StudyUnrecognizedFormatException {
            Element child = getChildWithNameOrNull(parent, name);
            if (child != null) {
                return child;
            } else {
                String message = "Not have found a element: \"" + name + "\" into \"" + parent.getName() + "\"";
                StudyUnrecognizedFormatException e = new StudyUnrecognizedFormatException(message);
                logger.warn(e);
                throw e;
            }
        }

        @Nullable
        static Element getChildWithNameOrNull(@NotNull Element parent, @NotNull String name) {
            for (Element child : parent.getChildren()) {
                Attribute attribute = child.getAttribute(NAME);
                if (attribute == null) {
                    continue;
                }
                if (name.equals(attribute.getValue())) {
                    return child;
                }
            }
            return null;
        }
    }

    public static class Json {
        private Json() {
        }

        public static class SupportedLanguagesSerializer implements JsonSerializer<SupportedLanguages> {
            @NotNull
            @Override
            public JsonElement serialize(SupportedLanguages src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        }

        public static class SupportedLanguagesDeserializer implements JsonDeserializer<SupportedLanguages> {
            @NotNull
            @Override
            public SupportedLanguages deserialize(
                    JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                String data = json.getAsString();

                try {
                    return SupportedLanguages.langOf(data);
                } catch (IllegalArgumentException e) {
                    return SupportedLanguages.INVALID;
                }
            }
        }
    }
}
