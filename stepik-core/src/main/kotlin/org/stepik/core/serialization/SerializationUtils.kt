package org.stepik.core.serialization

import com.google.gson.internal.LinkedTreeMap
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.annotations.XStreamOmitField
import com.thoughtworks.xstream.io.xml.DomDriver
import org.jdom.Element
import org.jdom.input.DOMBuilder
import org.jdom.output.XMLOutputter
import org.stepik.api.objects.courses.Course
import org.stepik.api.objects.lessons.CompoundUnitLesson
import org.stepik.api.objects.sections.Section
import org.stepik.api.objects.steps.Limit
import org.stepik.api.objects.steps.Sample
import org.stepik.api.objects.steps.Step
import org.stepik.api.objects.steps.VideoUrl
import org.stepik.api.objects.users.User
import org.stepik.core.BaseProjectManager
import org.stepik.core.SupportedLanguages
import org.stepik.core.courseFormat.CourseNode
import org.stepik.core.courseFormat.LessonNode
import org.stepik.core.courseFormat.SectionNode
import org.stepik.core.courseFormat.StepNode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

object SerializationUtils {
    @XStreamOmitField
    val xStream: XStream = {
        XStream(DomDriver()).apply {
            alias("CourseNode", CourseNode::class.java)
            alias("SectionNode", SectionNode::class.java)
            alias("LessonNode", LessonNode::class.java)
            alias("StepNode", StepNode::class.java)
            alias("Limit", Limit::class.java)
            alias("SupportedLanguages", SupportedLanguages::class.java)
            alias("VideoUrl", VideoUrl::class.java)
            alias("LinkedTreeMap", LinkedTreeMap::class.java)
            alias("Sample", Sample::class.java)
            alias("Course", Course::class.java)
            alias("Section", Section::class.java)
            alias("CompoundUnitLesson", CompoundUnitLesson::class.java)
            alias("Step", Step::class.java)
            alias("User", User::class.java)
            autodetectAnnotations(true)
            classLoader = BaseProjectManager::class.java.classLoader
            registerConverter(SupportedLanguagesConverter())
            registerConverter(SampleConverter())
            ignoreUnknownElements()
            setMode(XStream.ID_REFERENCES)
        }
    }.invoke()

    private val outputter: XMLOutputter by lazy {
        XMLOutputter()
    }

    private val factory: DocumentBuilderFactory by lazy {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isValidating = false
        factory
    }

    private val builder: DocumentBuilder by lazy {
        factory.newDocumentBuilder()
    }

    private val domBuilder: DOMBuilder by lazy {
        DOMBuilder()
    }

    fun elementToXml(state: Element, mainElement: String): String {
        return outputter.outputString(state.getChild(mainElement))
    }

    fun toElement(out: ByteArrayOutputStream): Element {
        ByteArrayInputStream(out.toByteArray()).use {
            val doc = builder.parse(it)
            val document = domBuilder.build(doc)

            val root = document.rootElement
            document.removeContent(root)

            val element = Element("element")
            element.addContent(root)
            return element
        }
    }
}
