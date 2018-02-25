package org.stepik.core.courseFormat

import com.intellij.openapi.project.Project
import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.steps.Limit
import org.stepik.api.objects.steps.Sample
import org.stepik.api.objects.steps.Step
import org.stepik.api.objects.submissions.Reply
import org.stepik.core.SupportedLanguages
import org.stepik.core.SupportedLanguages.Companion.langOfName
import org.stepik.core.SupportedLanguages.INVALID
import org.stepik.core.core.EduNames
import org.stepik.core.courseFormat.StepType.CODE
import java.time.Instant
import java.util.*

class StepNode(project: Project? = null, stepikApiClient: StepikApiClient? = null, data: StudyObject? = null) :
        Node(project, stepikApiClient, data) {

    @XStreamAlias("supportedLanguages")
    private var _supportedLanguages: List<SupportedLanguages>? = null

    val supportedLanguages: List<SupportedLanguages>
        get() {
            if (_supportedLanguages == null) {
                if (type === CODE) {
                    _supportedLanguages = (data as Step).block?.let {
                        it.options.codeTemplates.map {
                            langOfName(it.key)
                        }
                    }?.filter { it !== INVALID } ?: emptyList()
                } else {
                    return emptyList()
                }
            }

            return _supportedLanguages ?: throw AssertionError("Set to null by another thread")
        }

    @XStreamAlias("currentLang")
    private var _currentLang: SupportedLanguages? = null

    var currentLang: SupportedLanguages
        get() {
            val currentLang = _currentLang ?: INVALID
            if (currentLang === INVALID || currentLang !in supportedLanguages) {
                _currentLang = supportedLanguages.firstOrNull() ?: INVALID
            }
            return _currentLang ?: throw AssertionError("Set to null by another thread")
        }
        set(value) {
            _currentLang = value
        }

    private var courseId: Long = 0
    @XStreamOmitField
    var assignment: Long? = null
        get() {
            if (field == null) {
                val parent = parent
                if (parent is LessonNode) {
                    val data = parent.data as CompoundUnitLesson
                    val steps = data.lesson.steps.sorted()
                    val index = steps.indexOf(id)
                    if (index != -1) {
                        val assignments = data.unit.assignments
                        field = assignments.getOrNull(index)
                    }
                }
            }
            return field
        }

    @XStreamAlias("lastReply")
    private var _lastReply: Reply? = null

    var lastReply: Reply
        get() {
            if (_lastReply == null) {
                _lastReply = Reply()
            }
            return _lastReply ?: throw AssertionError("Set to null by another thread")
        }
        set(value) {
            _lastReply = value
            _lastReplyTime = Date()
        }

    @XStreamAlias("lastReplyTime")
    private var _lastReplyTime: Date? = null
    val lastReplyTime: Date
        get() {
            if (_lastReplyTime == null) {
                _lastReplyTime = Date.from(Instant.EPOCH)
            }
            return _lastReplyTime ?: throw AssertionError("Set to null by another thread")
        }

    var lastSubmissionId: Long = 0

    override val childClass: Class<StepNode>
        get() = StepNode::class.java

    override val childDataClass: Class<Step>
        get() = Step::class.java

    val text: String
        get() = (data as Step).block?.text ?: ""

    val currentTemplate: String
        get() = getTemplate(currentLang)

    private val limits: Map<String, Limit>
        get() = (data as Step).block?.options?.limits ?: emptyMap()

    val limit: Limit
        get() = limits.getOrDefault(currentLang.langName, Limit())

    override val dataClass: Class<Step>
        get() = Step::class.java

    val samples: List<Sample>
        get() {
            if (type === CODE) {
                return (data as Step).block?.options?.samples ?: emptyList()
            }

            return emptyList()
        }

    val type: StepType
        get() = StepType.of((data as Step).block?.name)

    override val directoryPrefix
        get() = EduNames.STEP

    override fun beforeInit() {
        _supportedLanguages = null
        courseId = 0
    }

    override fun loadData(stepikApiClient: StepikApiClient, id: Long): Boolean {
        try {
            val data = stepikApiClient.steps()
                    .get()
                    .id(id)
                    .execute()
                    .firstOrNull()
                    ?: Step().also { it.id = id }

            val oldData = this.data
            this.data = data
            return oldData.updateDate != data.updateDate
        } catch (logged: StepikClientException) {
            logger.warn(String.format("Failed step lesson data id=%d", id), logged)
        }

        return true
    }

    fun getTemplate(language: SupportedLanguages): String {
        val templates = (data as Step).block?.options?.codeTemplates ?: emptyMap()
        return templates.getOrDefault(language.langName, "")
    }

    override fun getCourseId(stepikApiClient: StepikApiClient): Long {
        val parent = parent
        if (parent != null) {
            return parent.getCourseId(stepikApiClient)
        }

        if (courseId != 0L) {
            return courseId
        }

        val lessonId = (data as Step).lesson
        if (lessonId == 0) {
            return 0
        }

        try {
            val unit = stepikApiClient.units()
                    .get()
                    .lesson(lessonId.toLong())
                    .execute()
                    .firstOrNull()
                    ?: return 0

            val lessonNode = LessonNode()
            val lessonData = lessonNode.data as CompoundUnitLesson
            lessonData.unit = unit

            courseId = lessonNode.getCourseId(stepikApiClient)
            return courseId
        } catch (e: StepikClientException) {
            logger.warn(e)
        }

        return 0
    }

    fun isStepFile(fileName: String): Boolean {
        return "${EduNames.SRC}/${currentLang.mainFileName}" == fileName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false

        val stepNode = other as StepNode?

        if (courseId != stepNode!!.courseId) return false

        return if (supportedLanguages != stepNode.supportedLanguages) false else {
            currentLang === stepNode.currentLang
        }
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + supportedLanguages.hashCode()
        result = 31 * result + currentLang.hashCode()
        result = 31 * result + (courseId xor courseId.ushr(32)).toInt()
        return result
    }

    fun cleanLastReply() {
        _lastReply = null
        lastSubmissionId = 0
        _lastReplyTime = null
    }
}
