package org.stepik.core.serialization;

import com.intellij.openapi.diagnostic.Logger;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class StudySerializationUtils {
    private static final Logger logger = Logger.getInstance(StudySerializationUtils.class);
    public static final String VALUE = "value";
    public static final String NAME = "name";
    public static final String VERSION = "version";
    public static final String COURSE = "course";
    public static final String COURSE_NODE = "courseNode";
    public static final String COURSE_CLASS = "Course";
    public static final String COURSE_NODE_CLASS = "CourseNode";
    public static final String ID = "id";
    public static final String OPTION = "option";
    public static final String DATA = "data";
    public static final String DESCRIPTION = "description";
    public static final String TITLE = "title";
    public static final String LIST = "list";
    public static final String SECTIONS = "sections";
    public static final String SECTION_NODE_CLASS = "SectionNode";
    public static final String SECTION_CLASS = "Section";
    public static final String POSITION = "position";
    public static final String LESSONS = "lessons";
    public static final String LESSON_CLASS = "Lesson";
    public static final String LESSON_NODE_CLASS = "LessonNode";
    public static final String UNIT_CLASS = "Unit";
    public static final String STEP_LIST = "stepList";
    public static final String STEP_NODE_CLASS = "StepNode";
    public static final String STEP_CLASS = "Step";
    public static final String BLOCK = "block";
    public static final String BLOCK_VIEW_CLASS = "BlockView";
    public static final String CODE = "code";
    public static final String TEXT = "text";
    public static final String SECTIONS_NODES = "sectionNodes";
    public static final String LESSON_NODES = "lessonNodes";
    public static final String STEP_NODES = "stepNodes";
    public static final String UNIT = "unit";
    public static final String TIME_LIMITS = "timeLimits";
    public static final String MAP = "map";
    public static final String ENTRY = "entry";
    public static final String KEY = "key";
    public static final String OPTIONS = "options";
    public static final String BLOCK_VIEW_OPTIONS = "BlockViewOptions";
    public static final String LIMITS = "limits";
    public static final String LIMIT_CLASS = "Limit";
    public static final String TIME = "time";
    public static final String MEMORY = "memory";
    public static final Pattern limitPattern = Pattern.compile(".*:\\s(\\d+)\\sMb.*:\\s(\\d+)s.*");
    public static final String DEFAULT_LANG = "defaultLang";
    public static final String CURRENT_LANG = "currentLang";
    public static final String SUPPORTED_LANGUAGES = "supportedLanguages";
    public static final String COMPOUND_UNIT_LESSON_CLASS = "CompoundUnitLesson";
    public static final String LESSON = "lesson";
    public static final String CHILDREN = "children";
    public static final String STEP_FILES = "stepFiles";
    public static final String ADAPTIVE = "adaptive";
    public static final String USER = "user";
    public static final String INVALID = "invalid";
    public static final String ROOT = "root";

    public static int getVersion(Element element, String mainElement) throws StudyUnrecognizedFormatException {
        final Element stepManager = element.getChild(mainElement);
        if (stepManager == null) {
            String message = "Can't get a version: not found element \"" + mainElement + "\"";
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
    public static Element getFieldWithName(@NotNull Element parent, @NotNull String name)
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
    public static Element getFieldWithNameOrNull(@NotNull Element parent, @NotNull String name) {
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

    public static List<Element> getListFieldWithNameOrNull(
            @NotNull Element parent,
            @NotNull String fieldName,
            @NotNull String className) {
        Element child = getFieldWithNameOrNull(parent, fieldName);
        if (child == null) {
            return null;
        }

        Element list = child.getChild(LIST);
        if (list == null) {
            return null;
        }

        return new ArrayList<>(list.getChildren(className));
    }

    @NotNull
    public static List<Element> getListFieldWithName(
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

    public static void moveIdAndNameAsTitle(@NotNull Element source, @NotNull Element target)
            throws StudyUnrecognizedFormatException {
        moveField(source, target, ID);
        moveField(source, target, NAME);
        renameField(target, NAME, TITLE);
    }

    @NotNull
    public static Map<String, String> getMapFieldWithName(@NotNull Element parent, @NotNull String fieldName)
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
    public static Element createField(@NotNull Element parent, @NotNull String name, @Nullable String value) {
        Element field = new Element(OPTION);
        field.setAttribute(NAME, name);
        if (value != null) {
            field.setAttribute(VALUE, value);
        }

        parent.addContent(field);
        return field;
    }

    @NotNull
    public static Element renameField(
            @NotNull Element object,
            @NotNull String oldFieldName,
            @NotNull String newFieldName) throws StudyUnrecognizedFormatException {
        Element field = getFieldWithName(object, oldFieldName);
        field.setAttribute(NAME, newFieldName);
        return field;
    }

    @NotNull
    public static Element changeClass(@NotNull Element field, @NotNull String oldClass, @NotNull String newClass) {
        Element courseTag = field.getChild(oldClass);
        courseTag.setName(newClass);
        return courseTag;
    }

    public static void moveField(@NotNull Element source, @NotNull Element target, @NotNull String fieldName)
            throws StudyUnrecognizedFormatException {
        Element field = getFieldWithName(source, fieldName);
        Element newField = field.clone();
        target.addContent(newField);
    }

    @NotNull
    public static Element createFieldWithClass(
            @NotNull Element parent,
            @NotNull String fieldName,
            @NotNull String className) {
        Element data = createField(parent, fieldName, null);
        Element dataCourseTag = new Element(className);
        data.addContent(dataCourseTag);

        return dataCourseTag;
    }

    public static List<Element> getSectionNodes(@NotNull Element stepManager) throws StudyUnrecognizedFormatException {
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
    public static Element getStepManager(@NotNull Element state, String mailElement) throws StudyUnrecognizedFormatException {
        final Element stepManager = state.getChild(mailElement);
        if (stepManager == null) {
            throw new StudyUnrecognizedFormatException("Not found element \"" + mailElement + "\"");
        }
        return stepManager;
    }

    @NotNull
    public static Element getCourseNode(@NotNull Element stepManager) throws StudyUnrecognizedFormatException {
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

    public static void removeOption(Element parent, String description) {
        Element removed = getFieldWithNameOrNull(parent, description);
        if (removed != null) {
            removeChild(parent, removed);
        }
    }

    public static void replaceLanguages(
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

    public static void replaceLanguage(@Nullable Attribute attribute) {
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

    public static void silentRenameField(@NotNull Element element, @NotNull String oldName, @NotNull String newName) {
        try {
            renameField(element, oldName, newName);
        } catch (StudyUnrecognizedFormatException e) {
            logger.warn(String.format("Can't rename %s to %s", oldName, newName), e);
        }
    }

    public static void removeChild(Element parent, Element child) {
        child.setName("WILL_DELETE");
        parent.removeChild(child.getName());
    }

    public static void convertToXStreamStyle(List<Element> elements) {
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

                    if (!children.isEmpty()) {
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

                addAttributeAsChild(element, keyAttr);

                Attribute valueAttr = element.getAttribute(VALUE);

                if (valueAttr != null) {
                    addAttributeAsChild(element, valueAttr);
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

    public static void addAttributeAsChild(Element element, Attribute attribute) {
        Element string = new Element("string");
        string.setText(attribute.getValue());
        element.removeAttribute(attribute);
        element.addContent(string);
    }
}
