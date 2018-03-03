package org.stepik.core.serialization

import org.jdom.Attribute
import org.jdom.Element
import org.stepik.core.common.Loggable


object StudySerializationUtils : Loggable {
    const val VALUE = "value"
    const val NAME = "name"
    private const val VERSION = "version"
    const val COURSE = "course"
    const val COURSE_NODE = "courseNode"
    const val COURSE_CLASS = "Course"
    const val COURSE_NODE_CLASS = "CourseNode"
    const val ID = "id"
    const val OPTION = "option"
    const val DATA = "data"
    const val DESCRIPTION = "description"
    private const val TITLE = "title"
    const val LIST = "list"
    const val SECTIONS = "sections"
    const val SECTION_NODE_CLASS = "SectionNode"
    const val SECTION_CLASS = "Section"
    const val POSITION = "position"
    const val LESSONS = "lessons"
    const val LESSON_CLASS = "Lesson"
    const val LESSON_NODE_CLASS = "LessonNode"
    const val UNIT_CLASS = "Unit"
    const val STEP_LIST = "stepList"
    const val STEP_NODE_CLASS = "StepNode"
    const val STEP_CLASS = "Step"
    const val BLOCK = "block"
    const val BLOCK_VIEW_CLASS = "BlockView"
    const val CODE = "code"
    const val TEXT = "text"
    const val SECTIONS_NODES = "sectionNodes"
    const val LESSON_NODES = "lessonNodes"
    const val STEP_NODES = "stepNodes"
    const val UNIT = "unit"
    const val TIME_LIMITS = "timeLimits"
    const val MAP = "map"
    const val ENTRY = "entry"
    const val KEY = "key"
    const val OPTIONS = "options"
    const val BLOCK_VIEW_OPTIONS = "BlockViewOptions"
    const val LIMITS = "limits"
    const val LIMIT_CLASS = "Limit"
    const val TIME = "time"
    const val MEMORY = "memory"
    val limitPattern = ".*:\\s(\\d+)\\sMb.*:\\s(\\d+)s.*".toRegex()
    private const val DEFAULT_LANG = "defaultLang"
    const val CURRENT_LANG = "currentLang"
    const val SUPPORTED_LANGUAGES = "supportedLanguages"
    const val COMPOUND_UNIT_LESSON_CLASS = "CompoundUnitLesson"
    const val LESSON = "lesson"
    const val CHILDREN = "children"
    const val STEP_FILES = "stepFiles"
    const val ADAPTIVE = "adaptive"
    const val USER = "user"
    const val INVALID = "invalid"
    const val ROOT = "root"

    fun getVersion(element: Element, mainElement: String): Int {
        val stepManager = element.getChild(mainElement)
        if (stepManager == null) {
            val message = "Can't get a version: not found element '$mainElement'"
            throw StudyUnrecognizedFormatException(message)
        }

        var versionElement: Element? = stepManager.getChild(VERSION)

        if (versionElement != null) {
            return parseVersion(versionElement.text)
        }

        versionElement = getChildFieldWithName(stepManager, VERSION)
        return parseVersion(versionElement.getAttributeValue(VALUE))
    }

    private fun parseVersion(version: String): Int {
        try {
            return version.toInt()
        } catch (e: NumberFormatException) {
            throw StudyUnrecognizedFormatException("Can't get a version: $version")
        }

    }

    private fun getChildFieldWithName(parent: Element, name: String): Element {
        val child = getChildFieldWithNameOrNull(parent, name)
        if (child != null) {
            return child
        } else {
            val message = "Not have found a element: '$name' into '${parent.name}'"
            val e = StudyUnrecognizedFormatException(message)
            logger.warn(e)
            throw e
        }
    }

    fun getChildFieldWithNameOrNull(element: Element, childName: String): Element? {
        return element.children.firstOrNull { it?.getAttribute(NAME)?.value == childName }
    }

    fun getListFieldWithNameOrNull(
            parent: Element,
            fieldName: String,
            className: String): List<Element>? {
        val child = getChildFieldWithNameOrNull(parent, fieldName) ?: return null

        val list = child.getChild(LIST) ?: return null

        return list.getChildren(className)
    }

    fun getListFieldWithName(
            parent: Element,
            fieldName: String,
            className: String): List<Element> {

        return getListFieldWithNameOrNull(parent, fieldName, className)
                ?: throw StudyUnrecognizedFormatException("Can't get a list: not found $fieldName ($className)")
    }

    fun moveIdAndNameAsTitle(source: Element, target: Element) {
        moveField(source, target, ID)
        moveField(source, target, NAME)
        renameField(target, NAME, TITLE)
    }

    fun getMapFieldWithName(parent: Element, fieldName: String): Map<String, String> {
        return getChildFieldWithName(parent, fieldName)
                .getChild(MAP)
                .getChildren(ENTRY)
                .associate {
                    it.getAttributeValue(KEY) to it.getAttributeValue(VALUE)
                }
    }

    fun createField(parent: Element, name: String, value: String?): Element {
        val field = Element(OPTION)
        field.setAttribute(NAME, name)
        if (value != null) {
            field.setAttribute(VALUE, value)
        }

        parent.addContent(field)
        return field
    }

    fun renameField(
            `object`: Element,
            oldFieldName: String,
            newFieldName: String): Element {
        val field = getChildFieldWithName(`object`, oldFieldName)
        field.setAttribute(NAME, newFieldName)
        return field
    }

    fun changeClass(field: Element, oldClass: String, newClass: String): Element {
        val courseTag = field.getChild(oldClass)
        courseTag.name = newClass
        return courseTag
    }

    fun moveField(source: Element, target: Element, fieldName: String) {
        val field = getChildFieldWithName(source, fieldName)
        val newField = field.clone()
        target.addContent(newField)
    }

    fun createFieldWithClass(
            parent: Element,
            fieldName: String,
            className: String): Element {
        val data = createField(parent, fieldName, null)
        val dataCourseTag = Element(className)
        data.addContent(dataCourseTag)

        return dataCourseTag
    }

    fun getSectionNodes(stepManager: Element): List<Element>? {
        val defaultLang = getChildFieldWithNameOrNull(stepManager, DEFAULT_LANG)
                ?: createField(stepManager, DEFAULT_LANG, INVALID)

        val defaultLangValue = defaultLang.getAttribute(VALUE)
        replaceLanguage(defaultLangValue)

        val courseNode = getCourseNode(stepManager)

        return getListFieldWithNameOrNull(courseNode, SECTIONS_NODES, SECTION_NODE_CLASS)
    }

    fun getStepManager(state: Element, mailElement: String): Element {
        return state.getChild(mailElement)
                ?: throw StudyUnrecognizedFormatException("Not found element '$mailElement'")
    }

    fun getCourseNode(stepManager: Element): Element {
        val courseNodeOption = getChildFieldWithNameOrNull(stepManager, COURSE_NODE)
                ?: throw StudyUnrecognizedFormatException("Field $COURSE_NODE don't found")

        return courseNodeOption.getChild(COURSE_NODE_CLASS)
                ?: throw StudyUnrecognizedFormatException("Field $COURSE_NODE is not $COURSE_NODE_CLASS")
    }

    fun removeOption(parent: Element, description: String) {
        val removed = getChildFieldWithNameOrNull(parent, description)
        if (removed != null) {
            removeChild(parent, removed)
        }
    }

    fun replaceLanguages(collection: Element?, collectionType: String, itemType: String, valueAttrName: String) {
        collection ?: return

        val items = collection.getChild(collectionType) ?: return
        items.getChildren(itemType).forEach { entry ->
            val value = entry.getAttribute(valueAttrName)
            replaceLanguage(value)
        }
    }

    fun replaceLanguage(attribute: Attribute?) {
        attribute ?: return

        when (attribute.value) {
            "java8" -> attribute.value = "Java 8"
            "python3" -> attribute.value = "Python 3"
        }
    }

    fun silentRenameField(element: Element, oldName: String, newName: String) {
        try {
            renameField(element, oldName, newName)
        } catch (e: StudyUnrecognizedFormatException) {
            logger.warn("Can't rename $oldName to $newName", e)
        }

    }

    fun removeChild(parent: Element, child: Element) {
        child.name = "WILL_DELETE"
        parent.removeChild(child.name)
    }

    fun convertToXStreamStyle(elements: List<Element>) {
        elements.forEach { element ->
            if (element.name == OPTION) {
                val nameAttr = element.getAttribute(NAME)
                if (nameAttr != null) {
                    val name = nameAttr.value
                    element.name = nameAttr.value
                    element.removeAttribute(nameAttr)

                    if (name == DATA) {
                        val dataClass = when (element.parentElement.name) {
                            COURSE_NODE_CLASS -> COURSE_CLASS
                            SECTION_NODE_CLASS -> SECTION_CLASS
                            LESSON_NODE_CLASS -> COMPOUND_UNIT_LESSON_CLASS
                            STEP_NODE_CLASS -> STEP_CLASS
                            else -> null
                        }
                        if (dataClass != null) {
                            element.setAttribute("class", dataClass)
                        }
                    }
                }

                val valueAttr = element.getAttribute(VALUE)
                if (valueAttr != null) {
                    element.text = valueAttr.value
                    element.removeAttribute(valueAttr)
                    if (nameAttr == null) {
                        element.name = "string"
                    }
                } else {
                    val children = element.children

                    if (children.isNotEmpty()) {
                        val child = children.first()
                        removeChild(element, child)
                        element.addContent(child.cloneContent())
                    }
                }
            } else if (element.name == ENTRY) {
                val keyAttr = element.getAttribute(KEY) ?: return@forEach

                addAttributeAsChild(element, keyAttr)

                val valueAttr = element.getAttribute(VALUE)

                if (valueAttr != null) {
                    addAttributeAsChild(element, valueAttr)
                } else {
                    val value = element.getChild(VALUE)
                    if (value != null) {
                        element.removeChild(value.name)
                        element.addContent(value.cloneContent())
                    }
                }
            }

            convertToXStreamStyle(element.children)
        }
    }

    private fun addAttributeAsChild(element: Element, attribute: Attribute) {
        val string = Element("string")
        string.text = attribute.value
        element.removeAttribute(attribute)
        element.addContent(string)
    }
}
