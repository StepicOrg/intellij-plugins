package com.jetbrains.tmp.learning.serialization;

import com.intellij.openapi.diagnostic.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudySerializationUtils {
    public final static String MAIN_ELEMENT = "StepikProjectManager";
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
    private static final String SECTION_NODE_CLASS = "SectionNode";
    private static final String SECTION_CLASS = "Section";
    private static final String POSITION = "position";
    private static final String LESSONS = "lessons";
    private static final String LESSON_CLASS = "Lesson";
    private static final String LESSON_NODE_CLASS = "LessonNode";
    private static final String UNIT_CLASS = "Unit";
    private static final String STEP_LIST = "stepList";
    private static final String STEP_NODE_CLASS = "StepNode";
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
    private static final String DEFAULT_LANG = "defaultLang";
    private static final String CURRENT_LANG = "currentLang";
    private static final String SUPPORTED_LANGUAGES = "supportedLanguages";
    private static final String COMPOUND_UNIT_LESSON_CLASS = "CompoundUnitLesson";
    private static final String LESSON = "lesson";
    private static final String CHILDREN = "children";
    private static final String STEP_FILES = "stepFiles";
    private static final String ADAPTIVE = "adaptive";
    private static final String USER = "user";
    private static final String INVALID = "invalid";
    private static final String ROOT = "root";

    public static int getVersion(Element element) throws StudyUnrecognizedFormatException {
        final Element stepManager = element.getChild(MAIN_ELEMENT);
        if (stepManager == null) {
            String message = "Can't get a version: not found element \"" + MAIN_ELEMENT + "\"";
            throw new StudyUnrecognizedFormatException(message);
        }

        Element versionElement = stepManager.getChild(VERSION);

        if (versionElement != null) {
            return parseVersion(versionElement.getText());
        }

        versionElement = getFieldWithName(stepManager, VERSION);
        return parseVersion(versionElement.getAttributeValue(VALUE));
    }

    private static int parseVersion(@NotNull String version) throws StudyUnrecognizedFormatException {
        try {
            return Integer.parseInt(version);
        } catch (NumberFormatException e) {
            throw new StudyUnrecognizedFormatException("Can't get a version: " + version);
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

    private static List<Element> getListFieldWithNameOrNull(
            @NotNull Element parent,
            @NotNull String fieldName,
            @NotNull String className) {
        Element child = getFieldWithNameOrNull(parent, fieldName);
        if (child == null) {
            return null;
        }

        List<Element> children = new ArrayList<>();
        Element list = child.getChild(LIST);
        if (list == null) {
            return null;
        }
        children.addAll(list.getChildren(className));

        return children;
    }

    @NotNull
    private static List<Element> getListFieldWithName(
            @NotNull Element parent,
            @NotNull String fieldName,
            @NotNull String className)
            throws StudyUnrecognizedFormatException {
        List<Element> children = getListFieldWithNameOrNull(parent, fieldName, className);

        if (children == null) {
            throw new StudyUnrecognizedFormatException(String.format("Can't get a list: not found %s (%s)",
                    fieldName,
                    className));
        }
        return children;
    }

    @NotNull
    public static Element convertToSecondVersion(@NotNull Element state) throws StudyUnrecognizedFormatException {
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
            section.setName(SECTION_NODE_CLASS);
            Element data = createFieldWithClass(section, DATA, SECTION_CLASS);
            moveIdAndNameAsTitle(section, data);
            moveField(section, data, POSITION);

            List<Element> lessons = getListFieldWithName(section, LESSONS, LESSON_CLASS);
            renameField(section, LESSONS, LESSON_NODES);

            for (Element lesson : lessons) {
                lesson.setName(LESSON_NODE_CLASS);
                data = createFieldWithClass(lesson, DATA, LESSON_CLASS);
                moveIdAndNameAsTitle(lesson, data);

                Element unit = createFieldWithClass(lesson, UNIT, UNIT_CLASS);
                moveField(lesson, unit, POSITION);

                List<Element> steps = getListFieldWithName(lesson, STEP_LIST, STEP_CLASS);
                renameField(lesson, STEP_LIST, STEP_NODES);

                for (Element step : steps) {
                    step.setName(STEP_NODE_CLASS);
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

    public static Element convertToThirdVersion(@NotNull Element state) throws StudyUnrecognizedFormatException {
        Element stepManager = getStepManager(state);
        Element courseNode = getCourseNode(stepManager);

        removeOption(courseNode, DESCRIPTION);
        removeOption(courseNode, NAME);
        removeOption(courseNode, ID);
        removeOption(courseNode, ADAPTIVE);
        removeOption(stepManager, USER);

        List<Element> sectionNodes = getSectionNodes(stepManager);
        if (sectionNodes == null) {
            return state;
        }

        sectionNodes.forEach(sectionNode -> {
            removeOption(sectionNode, ID);
            removeOption(sectionNode, NAME);
            removeOption(sectionNode, POSITION);
        });

        sectionNodes.stream()
                .map(sectionNode -> getListFieldWithNameOrNull(sectionNode, LESSON_NODES, LESSON_NODE_CLASS))
                .filter(Objects::nonNull)
                .forEach(lessonNodes -> {
                            lessonNodes.forEach(lessonNode -> {
                                removeOption(lessonNode, ID);
                                removeOption(lessonNode, NAME);
                                removeOption(lessonNode, POSITION);
                            });
                            lessonNodes.stream()
                                    .map(lessonNode -> getListFieldWithNameOrNull(lessonNode, STEP_NODES,
                                            STEP_NODE_CLASS))
                                    .filter(Objects::nonNull)
                                    .forEach(stepNodes -> {
                                        stepNodes.forEach(stepNode -> {
                                            removeOption(stepNode, ID);
                                            removeOption(stepNode, NAME);
                                            removeOption(stepNode, POSITION);
                                            removeOption(stepNode, STEP_FILES);
                                            removeOption(stepNode, TEXT);
                                            removeOption(stepNode, TIME_LIMITS);
                                        });
                                        stepNodes.forEach(stepNode -> {
                                            Element currentLang = getFieldWithNameOrNull(stepNode, CURRENT_LANG);
                                            if (currentLang != null) {
                                                Attribute currentLangValue = currentLang.getAttribute(VALUE);
                                                replaceLanguage(currentLangValue);
                                            }

                                            Element limits = getFieldWithNameOrNull(stepNode, LIMITS);
                                            replaceLanguages(limits, MAP, ENTRY, KEY);

                                            Element supportedLanguages;
                                            supportedLanguages = getFieldWithNameOrNull(stepNode, SUPPORTED_LANGUAGES);
                                            replaceLanguages(supportedLanguages, LIST, OPTION, VALUE);
                                        });
                                    });
                        }
                );

        return state;
    }

    private static List<Element> getSectionNodes(@NotNull Element stepManager) throws StudyUnrecognizedFormatException {
        Element defaultLang = getFieldWithNameOrNull(stepManager, DEFAULT_LANG);
        if (defaultLang == null) {
            defaultLang = createField(stepManager, DEFAULT_LANG, INVALID);
        }
        Attribute defaultLangValue = defaultLang.getAttribute(VALUE);
        replaceLanguage(defaultLangValue);

        Element courseNode = getCourseNode(stepManager);

        return getListFieldWithNameOrNull(courseNode, SECTIONS_NODES, SECTION_NODE_CLASS);
    }

    @NotNull
    private static Element getStepManager(@NotNull Element state) throws StudyUnrecognizedFormatException {
        final Element stepManager = state.getChild(MAIN_ELEMENT);
        if (stepManager == null) {
            throw new StudyUnrecognizedFormatException("Not found element \"" + MAIN_ELEMENT + "\"");
        }
        return stepManager;
    }

    @NotNull
    private static Element getCourseNode(@NotNull Element stepManager) throws StudyUnrecognizedFormatException {
        Element courseNodeOption = getFieldWithNameOrNull(stepManager, COURSE_NODE);
        if (courseNodeOption == null) {
            String message = String.format("Field %s don't found", COURSE_NODE);
            throw new StudyUnrecognizedFormatException(message);
        }

        Element courseNode = courseNodeOption.getChild(COURSE_NODE_CLASS);
        if (courseNode == null) {
            String message = String.format("Field %s is not %s", COURSE_NODE, COURSE_NODE_CLASS);
            throw new StudyUnrecognizedFormatException(message);
        }
        return courseNode;
    }

    private static void removeOption(Element parent, String description) {
        Element removed = getFieldWithNameOrNull(parent, description);
        if (removed != null) {
            removeChild(parent, removed);
        }
    }

    private static void replaceLanguages(
            @Nullable Element collection,
            @NotNull String collectionType,
            @NotNull String itemType,
            @NotNull String valueAttrName) {
        if (collection != null) {
            Element items = collection.getChild(collectionType);
            if (items != null) {
                List<Element> itemList = items.getChildren(itemType);
                itemList.forEach(entry -> {
                    Attribute value = entry.getAttribute(valueAttrName);
                    replaceLanguage(value);
                });
            }
        }
    }

    private static void replaceLanguage(@Nullable Attribute attribute) {
        if (attribute == null) {
            return;
        }

        switch (attribute.getValue()) {
            case "java8":
                attribute.setValue("Java 8");
                break;
            case "python3":
                attribute.setValue("Python 3");
                break;
        }
    }


    public static Element convertToFourthVersion(Element state) throws StudyUnrecognizedFormatException {
        Element stepManager = getStepManager(state);
        Element courseNode = getCourseNode(stepManager);
        List<Element> sectionNodes = getSectionNodes(stepManager);
        if (sectionNodes != null) {
            sectionNodes.stream()
                    .map(sectionNode -> getListFieldWithNameOrNull(sectionNode, LESSON_NODES, LESSON_NODE_CLASS))
                    .filter(Objects::nonNull)
                    .forEach(lessonNodes -> lessonNodes
                            .forEach(lessonNode -> {
                                Element data = getFieldWithNameOrNull(lessonNode, DATA);
                                Element dataClassTag;
                                if (data == null) {
                                    return;
                                }

                                dataClassTag = data.getChild(LESSON_CLASS);

                                Element dataCloned;
                                if (dataClassTag == null) {
                                    dataClassTag = createFieldWithClass(lessonNode, LESSON, LESSON_CLASS);
                                    dataCloned = dataClassTag.getParentElement();
                                } else {
                                    dataCloned = data.clone();
                                    dataCloned.getAttribute(NAME).setValue(LESSON);
                                }

                                dataClassTag.removeContent();

                                dataClassTag.addContent(dataCloned);
                                Element unit = getFieldWithNameOrNull(lessonNode, UNIT);
                                if (unit != null) {
                                    Element unitCloned = unit.clone();
                                    dataClassTag.addContent(unitCloned);
                                    removeChild(lessonNode, unit);
                                }

                                dataClassTag.setName(COMPOUND_UNIT_LESSON_CLASS);

                                List<Element> stepNodes = getListFieldWithNameOrNull(lessonNode, STEP_NODES,
                                        STEP_NODE_CLASS);
                                if (stepNodes == null) {
                                    return;
                                }
                                stepNodes.forEach(stepNode -> removeOption(stepNode, LIMITS));

                                silentRenameField(lessonNode, STEP_NODES, CHILDREN);
                            }));

            sectionNodes.forEach(sectionNode -> silentRenameField(sectionNode, LESSON_NODES, CHILDREN));

            silentRenameField(courseNode, SECTIONS_NODES, CHILDREN);
        }
        List<Element> elements = stepManager.getChildren();

        Element root = renameField(stepManager, COURSE_NODE, ROOT);
        root.setAttribute("class", COURSE_NODE_CLASS);

        convertToXStreamStyle(elements);

        return state;
    }

    private static void silentRenameField(@NotNull Element element, @NotNull String oldName, @NotNull String newName) {
        try {
            renameField(element, oldName, newName);
        } catch (StudyUnrecognizedFormatException e) {
            logger.warn(String.format("Can't rename %s to %s", oldName, newName), e);
        }
    }

    private static void removeChild(Element parent, Element child) {
        child.setName("WILL_DELETE");
        parent.removeChild(child.getName());
    }

    private static void convertToXStreamStyle(List<Element> elements) {
        elements.forEach(element -> {
            if (element.getName().equals(OPTION)) {
                Attribute nameAttr = element.getAttribute(NAME);
                if (nameAttr != null) {
                    String name = nameAttr.getValue();
                    element.setName(nameAttr.getValue());
                    element.removeAttribute(nameAttr);

                    if (name.equals(DATA)) {
                        Element parent = element.getParentElement();
                        String dataClass = null;
                        switch (parent.getName()) {
                            case COURSE_NODE_CLASS:
                                dataClass = COURSE_CLASS;
                                break;
                            case SECTION_NODE_CLASS:
                                dataClass = SECTION_CLASS;
                                break;
                            case LESSON_NODE_CLASS:
                                dataClass = COMPOUND_UNIT_LESSON_CLASS;
                                break;
                            case STEP_NODE_CLASS:
                                dataClass = STEP_CLASS;
                                break;
                        }
                        if (dataClass != null) {
                            element.setAttribute("class", dataClass);
                        }
                    }
                }

                Attribute valueAttr = element.getAttribute(VALUE);
                if (valueAttr != null) {
                    element.setText(valueAttr.getValue());
                    element.removeAttribute(valueAttr);
                    if (nameAttr == null) {
                        element.setName("string");
                    }
                } else {
                    List<Element> children = element.getChildren();

                    if (children.size() > 0) {
                        Element child = children.get(0);
                        removeChild(element, child);
                        element.addContent(child.cloneContent());
                    }
                }
            } else if (element.getName().equals(ENTRY)) {
                Attribute keyAttr = element.getAttribute(KEY);
                if (keyAttr == null) {
                    return;
                }

                element.removeAttribute(keyAttr);

                Attribute valueAttr = element.getAttribute(VALUE);

                Element string = new Element("string");
                string.setText(keyAttr.getValue());
                element.addContent(string);
                if (valueAttr != null) {
                    element.setText(valueAttr.getValue());
                    element.removeAttribute(valueAttr);
                } else {
                    Element value = element.getChild(VALUE);
                    if (value != null) {
                        element.removeChild(value.getName());
                        element.addContent(value.cloneContent());
                    }
                }

            }

            convertToXStreamStyle(element.getChildren());
        });
    }
}
