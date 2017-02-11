package com.jetbrains.tmp.learning.courseFormat;

import com.jetbrains.tmp.learning.StepikProjectManager;
import com.thoughtworks.xstream.XStream;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
import org.stepik.api.objects.steps.Limit;
import org.stepik.core.TestUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
public class SerializationTest {
    private static final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

    @Test
    public void serializeCourseNode() throws IOException, SAXException, ParserConfigurationException {
        CourseNode node = new CourseNode();
        node.getData();
        serialize("CourseNode", node);
    }

    @Test
    public void serializeSectionNode() throws IOException, SAXException, ParserConfigurationException {
        SectionNode node = new SectionNode();
        node.getData();
        LessonNode lessonNode = new LessonNode();
        lessonNode.setParent(node);
        CompoundUnitLesson data = lessonNode.getData();
        data.getLesson();
        data.getUnit();
        node.getLessonNodes().add(lessonNode);
        node.setLessonNodes(node.getLessonNodes());
        serialize("SectionNode", node);
    }

    @Test
    public void serializeLessonNode() throws IOException, SAXException, ParserConfigurationException {
        LessonNode node = new LessonNode();
        CompoundUnitLesson data = node.getData();
        data.getLesson();
        data.getUnit();
        serialize("LessonNode", node);
    }

    @Test
    public void serializeStepNode() throws IOException, SAXException, ParserConfigurationException {
        StepNode node = new StepNode();
        node.getData();
        Limit limit = new Limit();
        limit.setMemory(256);
        limit.setTime(8);
        node.getData().getBlock().getOptions().getLimits().put("Java 8", limit);
        serialize("StepNode", node);
    }

    private void serialize(@NotNull String name, @NotNull StudyNode<?> node)
            throws IOException, SAXException, ParserConfigurationException {
        XStream xs = StepikProjectManager.getXStream();

        String expected = xs.toXML(xs.fromXML(outputter.outputString(TestUtils.readXmlFile(SerializationTest.class,
                String.format("expected%s.xml", name)))));

        assertEquals(name, expected, xs.toXML(node));
    }
}
