package org.stepik.core.courseFormat;

import org.stepik.core.StepikProjectManager;
import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
import org.stepik.api.objects.steps.Limit;
import org.stepik.api.objects.steps.Step;
import org.stepik.core.TestUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author meanmail
 */
public class SerializationTest {

    @Test
    public void serializeCourseNode()
            throws IOException, SAXException, ParserConfigurationException, InstantiationException, IllegalAccessException {
        CourseNode node = new CourseNode();
        node.getData();
        serialize("CourseNode", node);
    }

    @Test
    public void serializeSectionNode()
            throws IOException, SAXException, ParserConfigurationException, InstantiationException, IllegalAccessException {
        SectionNode node = new SectionNode();
        node.getData();
        LessonNode lessonNode = new LessonNode();
        lessonNode.setParent(node);
        CompoundUnitLesson data = lessonNode.getData();
        assertNotNull(data);
        data.getLesson();
        data.getUnit();
        node.getChildren().add(lessonNode);
        serialize("SectionNode", node);
    }

    @Test
    public void serializeLessonNode()
            throws IOException, SAXException, ParserConfigurationException, InstantiationException, IllegalAccessException {
        LessonNode node = new LessonNode();
        CompoundUnitLesson data = node.getData();
        assertNotNull(data);
        data.getLesson();
        data.getUnit();
        serialize("LessonNode", node);
    }

    @Test
    public void serializeStepNode()
            throws IOException, SAXException, ParserConfigurationException, InstantiationException, IllegalAccessException {
        StepNode node = new StepNode();
        node.getData();
        node.setId(100);
        Limit limit = new Limit();
        limit.setMemory(256);
        limit.setTime(8);
        Step data = node.getData();
        assertNotNull(data);
        data.getBlock().getOptions().getLimits().put("Java 8", limit);
        serialize("StepNode", node);
    }

    private void serialize(@NotNull String name, @NotNull StudyNode node) throws IOException {
        XStream xs = StepikProjectManager.getXStream();

        String expected = TestUtils.readTextFile(SerializationTest.class, String.format("expected%s.xml", name));

        assertEquals(name, expected, xs.toXML(node));
    }
}
