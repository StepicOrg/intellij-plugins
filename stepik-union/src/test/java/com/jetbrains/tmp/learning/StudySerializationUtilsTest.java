package com.jetbrains.tmp.learning;

import org.stepik.core.serialization.StudySerializationUtils;
import org.stepik.core.serialization.StudyUnrecognizedFormatException;
import com.thoughtworks.xstream.XStream;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.stepik.core.StepikProjectManager;
import org.stepik.core.TestUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.stepik.core.serialization.StudySerializationUtils.MAIN_ELEMENT;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
public class StudySerializationUtilsTest {
    private static final int SOURCES_COUNT = 4;
    private static XMLOutputter outputter;

    @BeforeClass
    public static void before() {
        outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat().setLineSeparator("\n");
        outputter.setFormat(format);
    }

    private static Element readSourceXmlFile(int version, int index)
            throws IOException, SAXException, ParserConfigurationException {
        return TestUtils.readXmlFile(StudySerializationUtilsTest.class, format("version{0}_{1}.xml", version, index));
    }

    private static String readSourceTextFile(int version, int index) throws IOException {
        return TestUtils.readTextFile(StudySerializationUtilsTest.class, format("version{0}_{1}.xml", version, index));
    }

    @Test
    public void convertToSecondVersion() throws Exception {
        Element stateVersion1 = readSourceXmlFile(1, 4);
        Element stateVersion2 = StudySerializationUtils.convertToSecondVersion(stateVersion1);

        String actual = outputter.outputString(stateVersion2);
        String expected = readSourceTextFile(2, 4);

        assertEquals("#4", expected, actual);
    }

    @Test
    public void convertToThirdVersion() throws Exception {
        for (int i = 1; i <= SOURCES_COUNT; i++) {
            Element stateVersion2 = readSourceXmlFile(2, i);
            Element stateVersion3 = StudySerializationUtils.convertToThirdVersion(stateVersion2);

            String actual = outputter.outputString(stateVersion3);
            String expected = readSourceTextFile(3, i);

            assertEquals("#" + i, expected, actual);
        }
    }

    @Test(expected = StudyUnrecognizedFormatException.class)
    public void convertToThirdVersionWithException() throws Exception {
        Element stateVersion2 = TestUtils.readXmlFile(StudySerializationUtilsTest.class, "version2_0.xml");
        StudySerializationUtils.convertToThirdVersion(stateVersion2);
    }

    @Test
    public void convertToFourthVersion() throws Exception {
        for (int i = 1; i <= SOURCES_COUNT; i++) {
            Element stateVersion3 = readSourceXmlFile(3, i);
            Element stateVersion4 = StudySerializationUtils.convertToFourthVersion(stateVersion3);

            XStream xs = StepikProjectManager.getXStream();

            Object obj = xs.fromXML(outputter.outputString(stateVersion4.getChild(MAIN_ELEMENT)));

            String actual = xs.toXML(obj); //outputter.outputString(stateVersion4);
            String expected = readSourceTextFile(4, i);

            assertEquals("#" + i, expected, actual);
        }
    }
}