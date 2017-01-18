package com.jetbrains.tmp.learning;

import com.intellij.openapi.diagnostic.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StudySerializationUtils {

    final static String MAIN_ELEMENT = "StepikProjectManager";
    private static final Logger logger = Logger.getInstance(StudySerializationUtils.class);
    private static final String VALUE = "value";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String COURSE = "course";
    private static final String COURSE_NODE = "courseNode";
    private static final String COURSE_CLASS = "Course";
    private static final String COURSE_NODE_CLASS = "CourseNode";
    private static final String ID = "id";
    private static final String OPTION = "option";
    private static final String DATA = "data";
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private static final String LIST = "list";
    private static final String SECTIONS = "sections";
    private static final String SECTION_NODE = "SectionNode";
    private static final String SECTION_CLASS = "Section";
    private static final String POSITION = "position";
    private static final String LESSONS = "lessons";
    private static final String LESSON_CLASS = "Lesson";
    private static final String LESSON_NODE = "LessonNode";
    private static final String UNIT_CLASS = "Unit";
    private static final String STEP_LIST = "stepList";
    private static final String STEP_NODE = "StepNode";
    private static final String STEP_CLASS = "Step";
    private static final String BLOCK = "block";
    private static final String BLOCK_VIEW_CLASS = "BlockView";
    private static final String CODE = "code";
    private static final String TEXT = "text";
    private static final String SECTIONS_NODES = "sectionNodes";
    private static final String LESSON_NODES = "lessonNodes";
    private static final String STEP_NODES = "stepNodes";
    private static final String UNIT = "unit";
    private static final String TIME_LIMITS = "timeLimits";
    private static final String MAP = "map";
    private static final String ENTRY = "entry";
    private static final String KEY = "key";
    private static final String OPTIONS = "options";
    private static final String BLOCK_VIEW_OPTIONS = "BlockViewOptions";
    private static final String LIMITS = "limits";
    private static final String LIMIT_CLASS = "Limit";
    private static final String TIME = "time";
    private static final String MEMORY = "memory";
    private static final Pattern limitPattern = Pattern.compile(".*:\\s(\\d+)\\sMb.*:\\s(\\d+)s.*");

    static int getVersion(Element element) throws StudyUnrecognizedFormatException {
        final Element stepManager = element.getChild(MAIN_ELEMENT);
        if (stepManager == null) {
            String message = "Can't get a version: not found element \"" + MAIN_ELEMENT + "\"";
            throw new StudyUnrecognizedFormatException(message);
        }
        Element versionElement = getFieldWithName(stepManager, VERSION);
        try {
            return Integer.valueOf(versionElement.getAttributeValue(VALUE));
        } catch (NumberFormatException e) {
            throw new StudyUnrecognizedFormatException("Can't get a version: " + versionElement.toString());
        }
    }

    @NotNull
    private static Element getFieldWithName(@NotNull Element parent, @NotNull String name)
            throws StudyUnrecognizedFormatException {
        Element child = getFieldWithNameOrNull(parent, name);
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
    private static Element getFieldWithNameOrNull(@NotNull Element parent, @NotNull String name) {
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

    @NotNull
    private static List<Element> getListFieldWithName(
            @NotNull Element parent,
            @NotNull String fieldName,
            @NotNull String className)
            throws StudyUnrecognizedFormatException {
        List<Element> children = new ArrayList<>();

        Element child = getFieldWithName(parent, fieldName);
        Element list = child.getChild(LIST);
        children.addAll(list.getChildren(className));

        return children;
    }

    @NotNull
    static Element convertToSecondVersion(@NotNull Element state) throws StudyUnrecognizedFormatException {
        final Element stepManager = state.getChild(MAIN_ELEMENT);

        if (getFieldWithNameOrNull(stepManager, COURSE) == null) {
            return state;
        }
        Element course = renameField(stepManager, COURSE, COURSE_NODE);
        Element courseTag = changeClass(course, COURSE_CLASS, COURSE_NODE_CLASS);
        Element dataCourse = createFieldWithClass(courseTag, DATA, COURSE_CLASS);
        moveField(courseTag, dataCourse, DESCRIPTION);
        moveIdAndNameAsTitle(courseTag, dataCourse);

        List<Element> sections = getListFieldWithName(courseTag, SECTIONS, SECTION_CLASS);
        renameField(courseTag, SECTIONS, SECTIONS_NODES);

        for (Element section : sections) {
            section.setName(SECTION_NODE);
            Element data = createFieldWithClass(section, DATA, SECTION_CLASS);
            moveIdAndNameAsTitle(section, data);
            moveField(section, data, POSITION);

            List<Element> lessons = getListFieldWithName(section, LESSONS, LESSON_CLASS);
            renameField(section, LESSONS, LESSON_NODES);

            for (Element lesson : lessons) {
                lesson.setName(LESSON_NODE);
                data = createFieldWithClass(lesson, DATA, LESSON_CLASS);
                moveIdAndNameAsTitle(lesson, data);

                Element unit = createFieldWithClass(lesson, UNIT, UNIT_CLASS);
                moveField(lesson, unit, POSITION);

                List<Element> steps = getListFieldWithName(lesson, STEP_LIST, STEP_CLASS);
                renameField(lesson, STEP_LIST, STEP_NODES);

                for (Element step : steps) {
                    step.setName(STEP_NODE);
                    data = createFieldWithClass(step, DATA, STEP_CLASS);
                    moveField(step, data, ID);
                    moveField(step, data, POSITION);

                    Element block = createFieldWithClass(data, BLOCK, BLOCK_VIEW_CLASS);
                    createField(block, NAME, CODE);
                    moveField(step, block, TEXT);

                    Map<String, String> timeLimits = getMapFieldWithName(step, TIME_LIMITS);

                    Element options = createFieldWithClass(block, OPTIONS, BLOCK_VIEW_OPTIONS);
                    Element limits = createFieldWithClass(options, LIMITS, MAP);

                    for (Map.Entry<String, String> entryLimit : timeLimits.entrySet()) {
                        Element entry = new Element(ENTRY);
                        entry.setAttribute(KEY, entryLimit.getKey());
                        limits.addContent(entry);

                        Element valueTag = new Element(VALUE);
                        entry.addContent(valueTag);

                        Element classTag = new Element(LIMIT_CLASS);
                        valueTag.addContent(classTag);

                        String value = entryLimit.getValue();
                        Matcher matcher = limitPattern.matcher(value);

                        String time = "0";
                        String memory = "0";

                        if (matcher.matches()) {
                            memory = matcher.group(1);
                            time = matcher.group(2);
                        }

                        createField(classTag, TIME, time);
                        createField(classTag, MEMORY, memory);
                    }
                }
            }
        }

        return state;
    }

    private static void moveIdAndNameAsTitle(@NotNull Element source, @NotNull Element target)
            throws StudyUnrecognizedFormatException {
        moveField(source, target, ID);
        moveField(source, target, NAME);
        renameField(target, NAME, TITLE);
    }

    @NotNull
    private static Map<String, String> getMapFieldWithName(@NotNull Element parent, @NotNull String fieldName)
            throws StudyUnrecognizedFormatException {
        HashMap<String, String> result = new HashMap<>();

        Element field = getFieldWithName(parent, fieldName);
        Element map = field.getChild(MAP);

        for (Element entry : map.getChildren(ENTRY)) {
            result.put(entry.getAttributeValue(KEY), entry.getAttributeValue(VALUE));
        }

        return result;
    }

    @NotNull
    private static Element createField(@NotNull Element parent, @NotNull String name, @Nullable String value) {
        Element field = new Element(OPTION);
        field.setAttribute(NAME, name);
        if (value != null) {
            field.setAttribute(VALUE, value);
        }

        parent.addContent(field);
        return field;
    }

    @NotNull
    private static Element renameField(
            @NotNull Element object,
            @NotNull String oldFieldName,
            @NotNull String newFieldName) throws StudyUnrecognizedFormatException {
        Element field = getFieldWithName(object, oldFieldName);
        field.setAttribute(NAME, newFieldName);
        return field;
    }

    @NotNull
    private static Element changeClass(@NotNull Element field, @NotNull String oldClass, @NotNull String newClass) {
        Element courseTag = field.getChild(oldClass);
        courseTag.setName(newClass);
        return courseTag;
    }

    private static void moveField(@NotNull Element source, @NotNull Element target, @NotNull String fieldName)
            throws StudyUnrecognizedFormatException {
        Element field = getFieldWithName(source, fieldName);
        Element newField = field.clone();
        target.addContent(newField);
    }

    @NotNull
    private static Element createFieldWithClass(
            @NotNull Element parent,
            @NotNull String fieldName,
            @NotNull String className) {
        Element data = createField(parent, fieldName, null);
        Element dataCourseTag = new Element(className);
        data.addContent(dataCourseTag);

        return dataCourseTag;
    }

    static class StudyUnrecognizedFormatException extends Exception {
        StudyUnrecognizedFormatException(String message) {
            super(message);
        }
    }
}
