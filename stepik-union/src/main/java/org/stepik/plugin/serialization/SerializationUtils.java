package org.stepik.plugin.serialization;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.serialization.StudyUnrecognizedFormatException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

import static org.stepik.core.serialization.StudySerializationUtils.*;

public class SerializationUtils {
    public final static String MAIN_ELEMENT = "StepikProjectManager";

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

    public static Element convertToThirdVersion(@NotNull Element state) throws StudyUnrecognizedFormatException {
        Element stepManager = getStepManager(state, MAIN_ELEMENT);
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

    public static Element convertToFourthVersion(Element state) throws StudyUnrecognizedFormatException {
        Element stepManager = getStepManager(state, MAIN_ELEMENT);
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
}
