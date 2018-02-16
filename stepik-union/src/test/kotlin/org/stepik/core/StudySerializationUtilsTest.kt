package org.stepik.core

import org.jdom.Element
import org.jdom.output.Format
import org.jdom.output.XMLOutputter
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.stepik.core.serialization.StudySerializationUtils
import org.stepik.core.serialization.StudySerializationUtils.MAIN_ELEMENT
import org.stepik.core.serialization.StudyUnrecognizedFormatException
import org.xml.sax.SAXException
import java.io.IOException
import java.text.MessageFormat.format
import javax.xml.parsers.ParserConfigurationException


class StudySerializationUtilsTest {

    @Test
    @Throws(Exception::class)
    fun convertToSecondVersion() {
        val stateVersion1 = readSourceXmlFile(1, 4)
        val stateVersion2 = StudySerializationUtils.convertToSecondVersion(stateVersion1)

        val actual = outputter!!.outputString(stateVersion2)
        val expected = readSourceTextFile(2, 4)

        assertEquals("#4", expected, actual)
    }

    @Test
    @Throws(Exception::class)
    fun convertToThirdVersion() {
        for (i in 1..SOURCES_COUNT) {
            val stateVersion2 = readSourceXmlFile(2, i)
            val stateVersion3 = StudySerializationUtils.convertToThirdVersion(stateVersion2)

            val actual = outputter!!.outputString(stateVersion3)
            val expected = readSourceTextFile(3, i)

            assertEquals("#" + i, expected, actual)
        }
    }

    @Test(expected = StudyUnrecognizedFormatException::class)
    @Throws(Exception::class)
    fun convertToThirdVersionWithException() {
        val stateVersion2 = TestUtils.readXmlFile(StudySerializationUtilsTest::class.java, "version2_0.xml")
        StudySerializationUtils.convertToThirdVersion(stateVersion2)
    }

    @Test
    @Throws(Exception::class)
    fun convertToFourthVersion() {
        for (i in 1..SOURCES_COUNT) {
            val stateVersion3 = readSourceXmlFile(3, i)
            val stateVersion4 = StudySerializationUtils.convertToFourthVersion(stateVersion3)

            val xs = StepikProjectManager.xStream

            val obj = xs.fromXML(outputter!!.outputString(stateVersion4.getChild(MAIN_ELEMENT)))

            val actual = xs.toXML(obj) //outputter.outputString(stateVersion4);
            val expected = readSourceTextFile(4, i)

            assertEquals("#" + i, expected, actual)
        }
    }

    companion object {
        private const val SOURCES_COUNT = 4
        private var outputter: XMLOutputter? = null

        @BeforeClass
        @JvmStatic
        fun before() {
            outputter = XMLOutputter()
            val format = Format.getPrettyFormat().setLineSeparator("\n")
            outputter!!.format = format
        }

        @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
        private fun readSourceXmlFile(version: Int, index: Int): Element {
            return TestUtils.readXmlFile(StudySerializationUtilsTest::class.java, format("version{0}_{1}.xml", version, index))
        }

        @Throws(IOException::class)
        private fun readSourceTextFile(version: Int, index: Int): String {
            return TestUtils.readTextFile(StudySerializationUtilsTest::class.java, format("version{0}_{1}.xml", version, index))
        }
    }
}
