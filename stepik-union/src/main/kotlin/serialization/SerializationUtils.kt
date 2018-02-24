package serialization

import org.jdom.Element
import org.stepik.core.serialization.StudySerializationUtils.ADAPTIVE
import org.stepik.core.serialization.StudySerializationUtils.BLOCK
import org.stepik.core.serialization.StudySerializationUtils.BLOCK_VIEW_CLASS
import org.stepik.core.serialization.StudySerializationUtils.BLOCK_VIEW_OPTIONS
import org.stepik.core.serialization.StudySerializationUtils.CHILDREN
import org.stepik.core.serialization.StudySerializationUtils.CODE
import org.stepik.core.serialization.StudySerializationUtils.COMPOUND_UNIT_LESSON_CLASS
import org.stepik.core.serialization.StudySerializationUtils.COURSE
import org.stepik.core.serialization.StudySerializationUtils.COURSE_CLASS
import org.stepik.core.serialization.StudySerializationUtils.COURSE_NODE
import org.stepik.core.serialization.StudySerializationUtils.COURSE_NODE_CLASS
import org.stepik.core.serialization.StudySerializationUtils.CURRENT_LANG
import org.stepik.core.serialization.StudySerializationUtils.DATA
import org.stepik.core.serialization.StudySerializationUtils.DESCRIPTION
import org.stepik.core.serialization.StudySerializationUtils.ENTRY
import org.stepik.core.serialization.StudySerializationUtils.ID
import org.stepik.core.serialization.StudySerializationUtils.KEY
import org.stepik.core.serialization.StudySerializationUtils.LESSON
import org.stepik.core.serialization.StudySerializationUtils.LESSONS
import org.stepik.core.serialization.StudySerializationUtils.LESSON_CLASS
import org.stepik.core.serialization.StudySerializationUtils.LESSON_NODES
import org.stepik.core.serialization.StudySerializationUtils.LESSON_NODE_CLASS
import org.stepik.core.serialization.StudySerializationUtils.LIMITS
import org.stepik.core.serialization.StudySerializationUtils.LIMIT_CLASS
import org.stepik.core.serialization.StudySerializationUtils.LIST
import org.stepik.core.serialization.StudySerializationUtils.MAP
import org.stepik.core.serialization.StudySerializationUtils.MEMORY
import org.stepik.core.serialization.StudySerializationUtils.NAME
import org.stepik.core.serialization.StudySerializationUtils.OPTION
import org.stepik.core.serialization.StudySerializationUtils.OPTIONS
import org.stepik.core.serialization.StudySerializationUtils.POSITION
import org.stepik.core.serialization.StudySerializationUtils.ROOT
import org.stepik.core.serialization.StudySerializationUtils.SECTIONS
import org.stepik.core.serialization.StudySerializationUtils.SECTIONS_NODES
import org.stepik.core.serialization.StudySerializationUtils.SECTION_CLASS
import org.stepik.core.serialization.StudySerializationUtils.SECTION_NODE_CLASS
import org.stepik.core.serialization.StudySerializationUtils.STEP_CLASS
import org.stepik.core.serialization.StudySerializationUtils.STEP_FILES
import org.stepik.core.serialization.StudySerializationUtils.STEP_LIST
import org.stepik.core.serialization.StudySerializationUtils.STEP_NODES
import org.stepik.core.serialization.StudySerializationUtils.STEP_NODE_CLASS
import org.stepik.core.serialization.StudySerializationUtils.SUPPORTED_LANGUAGES
import org.stepik.core.serialization.StudySerializationUtils.TEXT
import org.stepik.core.serialization.StudySerializationUtils.TIME
import org.stepik.core.serialization.StudySerializationUtils.TIME_LIMITS
import org.stepik.core.serialization.StudySerializationUtils.UNIT
import org.stepik.core.serialization.StudySerializationUtils.UNIT_CLASS
import org.stepik.core.serialization.StudySerializationUtils.USER
import org.stepik.core.serialization.StudySerializationUtils.VALUE
import org.stepik.core.serialization.StudySerializationUtils.changeClass
import org.stepik.core.serialization.StudySerializationUtils.convertToXStreamStyle
import org.stepik.core.serialization.StudySerializationUtils.createField
import org.stepik.core.serialization.StudySerializationUtils.createFieldWithClass
import org.stepik.core.serialization.StudySerializationUtils.getChildFieldWithNameOrNull
import org.stepik.core.serialization.StudySerializationUtils.getCourseNode
import org.stepik.core.serialization.StudySerializationUtils.getListFieldWithName
import org.stepik.core.serialization.StudySerializationUtils.getListFieldWithNameOrNull
import org.stepik.core.serialization.StudySerializationUtils.getMapFieldWithName
import org.stepik.core.serialization.StudySerializationUtils.getSectionNodes
import org.stepik.core.serialization.StudySerializationUtils.getStepManager
import org.stepik.core.serialization.StudySerializationUtils.limitPattern
import org.stepik.core.serialization.StudySerializationUtils.moveField
import org.stepik.core.serialization.StudySerializationUtils.moveIdAndNameAsTitle
import org.stepik.core.serialization.StudySerializationUtils.removeChild
import org.stepik.core.serialization.StudySerializationUtils.removeOption
import org.stepik.core.serialization.StudySerializationUtils.renameField
import org.stepik.core.serialization.StudySerializationUtils.replaceLanguage
import org.stepik.core.serialization.StudySerializationUtils.replaceLanguages
import org.stepik.core.serialization.StudySerializationUtils.silentRenameField


object SerializationUtils {
    const val MAIN_ELEMENT = "StepikProjectManager"

    fun convertToSecondVersion(state: Element): Element {
        val stepManager = state.getChild(MAIN_ELEMENT)

        getChildFieldWithNameOrNull(stepManager, COURSE) ?: return state

        val course = renameField(stepManager, COURSE, COURSE_NODE)
        val courseTag = changeClass(course, COURSE_CLASS, COURSE_NODE_CLASS)
        val dataCourse = createFieldWithClass(courseTag, DATA, COURSE_CLASS)
        moveField(courseTag, dataCourse, DESCRIPTION)
        moveIdAndNameAsTitle(courseTag, dataCourse)

        val sections = getListFieldWithName(courseTag, SECTIONS, SECTION_CLASS)
        renameField(courseTag, SECTIONS, SECTIONS_NODES)

        sections.map { section ->
            section.name = SECTION_NODE_CLASS
            val data = createFieldWithClass(section, DATA, SECTION_CLASS)
            moveIdAndNameAsTitle(section, data)
            moveField(section, data, POSITION)

            val lessons = getListFieldWithName(section, LESSONS, LESSON_CLASS)
            renameField(section, LESSONS, LESSON_NODES)
            return@map lessons
        }
                .flatten()
                .map { lesson ->
                    lesson.name = LESSON_NODE_CLASS
                    val data = createFieldWithClass(lesson, DATA, LESSON_CLASS)
                    moveIdAndNameAsTitle(lesson, data)

                    val unit = createFieldWithClass(lesson, UNIT, UNIT_CLASS)
                    moveField(lesson, unit, POSITION)

                    val steps = getListFieldWithName(lesson, STEP_LIST, STEP_CLASS)
                    renameField(lesson, STEP_LIST, STEP_NODES)
                    return@map steps
                }
                .flatten()
                .forEach { step ->
                    step.name = STEP_NODE_CLASS
                    val data = createFieldWithClass(step, DATA, STEP_CLASS)
                    moveField(step, data, ID)
                    moveField(step, data, POSITION)

                    val block = createFieldWithClass(data, BLOCK, BLOCK_VIEW_CLASS)
                    createField(block, NAME, CODE)
                    moveField(step, block, TEXT)

                    val timeLimits = getMapFieldWithName(step, TIME_LIMITS)

                    val options = createFieldWithClass(block, OPTIONS, BLOCK_VIEW_OPTIONS)
                    val limits = createFieldWithClass(options, LIMITS, MAP)

                    timeLimits.entries.forEach { entryLimit ->
                        val entry = Element(ENTRY)
                        entry.setAttribute(KEY, entryLimit.key)
                        limits.addContent(entry)

                        val valueTag = Element(VALUE)
                        entry.addContent(valueTag)

                        val classTag = Element(LIMIT_CLASS)
                        valueTag.addContent(classTag)

                        val value = entryLimit.value
                        val matcher = limitPattern.matchEntire(value)

                        val time: String = matcher?.groups?.get(2)?.value ?: "0"
                        val memory = matcher?.groups?.get(1)?.value ?: "0"

                        createField(classTag, TIME, time)
                        createField(classTag, MEMORY, memory)
                    }
                }

        return state
    }

    fun convertToThirdVersion(state: Element): Element {
        val stepManager = getStepManager(state, MAIN_ELEMENT)
        val courseNode = getCourseNode(stepManager)

        removeOption(courseNode, DESCRIPTION)
        removeOption(courseNode, NAME)
        removeOption(courseNode, ID)
        removeOption(courseNode, ADAPTIVE)
        removeOption(stepManager, USER)

        val sectionNodes = getSectionNodes(stepManager) ?: return state

        sectionNodes.mapNotNull { sectionNode ->
            removeOption(sectionNode, ID)
            removeOption(sectionNode, NAME)
            removeOption(sectionNode, POSITION)
            getListFieldWithNameOrNull(sectionNode, LESSON_NODES, LESSON_NODE_CLASS)
        }
                .flatten()
                .mapNotNull { lessonNode ->
                    removeOption(lessonNode, ID)
                    removeOption(lessonNode, NAME)
                    removeOption(lessonNode, POSITION)
                    getListFieldWithNameOrNull(lessonNode, STEP_NODES, STEP_NODE_CLASS)
                }
                .flatten()
                .forEach { stepNode ->
                    removeOption(stepNode, ID)
                    removeOption(stepNode, NAME)
                    removeOption(stepNode, POSITION)
                    removeOption(stepNode, STEP_FILES)
                    removeOption(stepNode, TEXT)
                    removeOption(stepNode, TIME_LIMITS)

                    val currentLang = getChildFieldWithNameOrNull(stepNode, CURRENT_LANG)
                    if (currentLang != null) {
                        replaceLanguage(currentLang.getAttribute(VALUE))
                    }

                    val limits = getChildFieldWithNameOrNull(stepNode, LIMITS)
                    replaceLanguages(limits, MAP, ENTRY, KEY)

                    val supportedLanguages = getChildFieldWithNameOrNull(stepNode, SUPPORTED_LANGUAGES)
                    replaceLanguages(supportedLanguages, LIST, OPTION, VALUE)
                }

        return state
    }

    fun convertToFourthVersion(state: Element): Element {
        val stepManager = getStepManager(state, MAIN_ELEMENT)
        val courseNode = getCourseNode(stepManager)
        val sectionNodes = getSectionNodes(stepManager)
        if (sectionNodes != null) {
            sectionNodes.mapNotNull { sectionNode ->
                getListFieldWithNameOrNull(sectionNode, LESSON_NODES, LESSON_NODE_CLASS)
            }
                    .flatten()
                    .forEach { lessonNode ->
                        val data = getChildFieldWithNameOrNull(lessonNode, DATA) ?: return@forEach
                        var dataClassTag = data.getChild(LESSON_CLASS)

                        val dataCloned: Element
                        if (dataClassTag == null) {
                            dataClassTag = createFieldWithClass(lessonNode, LESSON, LESSON_CLASS)
                            dataCloned = dataClassTag.parentElement
                        } else {
                            dataCloned = data.clone()
                            dataCloned.getAttribute(NAME).value = LESSON
                        }

                        dataClassTag.removeContent()

                        dataClassTag.addContent(dataCloned)
                        val unit = getChildFieldWithNameOrNull(lessonNode, UNIT)
                        if (unit != null) {
                            val unitCloned = unit.clone()
                            dataClassTag.addContent(unitCloned)
                            removeChild(lessonNode, unit)
                        }

                        dataClassTag.name = COMPOUND_UNIT_LESSON_CLASS

                        val stepNodes = getListFieldWithNameOrNull(lessonNode, STEP_NODES, STEP_NODE_CLASS)
                                ?: return@forEach
                        stepNodes.forEach { removeOption(it, LIMITS) }

                        silentRenameField(lessonNode, STEP_NODES, CHILDREN)
                    }

            sectionNodes.forEach { silentRenameField(it, LESSON_NODES, CHILDREN) }
            silentRenameField(courseNode, SECTIONS_NODES, CHILDREN)
        }
        val root = renameField(stepManager, COURSE_NODE, ROOT)
        root.setAttribute("class", COURSE_NODE_CLASS)

        convertToXStreamStyle(stepManager.children)

        return state
    }
}
