package com.jetbrains.tmp.learning;

import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author meanmail
 */
public class StudySerializationUtilsTest {
    private static final int SOURCES_COUNT = 3;
    private static final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

    private static Element readXmlFile(@NotNull String filename)
            throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(StudySerializationUtilsTest.class
                .getResourceAsStream("convertToThirdVersion/" + filename));
        DOMBuilder domBuilder = new DOMBuilder();

        return domBuilder.build(doc).getRootElement();
    }

    @Test
    public void convertToThirdVersion() throws Exception {
        for (int i = 1; i <= SOURCES_COUNT; i++) {
            String sourceFileName = String.format("source_%d.xml", i);
            Element stateVersion2 = readXmlFile(sourceFileName);
            Element stateVersion3 = StudySerializationUtils.convertToThirdVersion(stateVersion2);

            String expected = outputter.outputString(stateVersion3);
            String actual = outputter.outputString(readXmlFile(String.format("expected_%d.xml", i)));

            assertEquals(sourceFileName, expected, actual);
        }
    }

    @Test(expected = StudySerializationUtils.StudyUnrecognizedFormatException.class)
    public void convertToThirdVersionWithException() throws Exception {
        Element stateVersion2 = readXmlFile("source.xml");
        StudySerializationUtils.convertToThirdVersion(stateVersion2);
    }

}