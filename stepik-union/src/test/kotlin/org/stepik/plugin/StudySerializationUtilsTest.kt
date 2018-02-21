package org.stepik.plugin

import org.jdom.Element
import org.jdom.output.Format
import org.jdom.output.XMLOutputter
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.stepik.core.TestUtils.readTextFile
import org.stepik.core.TestUtils.readXmlFile
import org.stepik.core.serialization.SerializationUtils.xStream
import org.stepik.core.serialization.StudyUnrecognizedFormatException
import org.stepik.plugin.serialization.SerializationUtils
import java.text.MessageFormat.format


class StudySerializationUtilsTest {

    @Test
    fun convertToSecondVersion() {
        val stateVersion1 = readSourceXmlFile(1, 4)
        val stateVersion2 = SerializationUtils.convertToSecondVersion(stateVersion1)

        val actual = outputter.outputString(stateVersion2)
        val expected = readSourceTextFile(2, 4)

        assertEquals("#4", expected, actual)
    }

    @Test
    fun convertToThirdVersion() {
        for (i in 1..SOURCES_COUNT) {
            val stateVersion2 = readSourceXmlFile(2, i)
            val stateVersion3 = SerializationUtils.convertToThirdVersion(stateVersion2)

            val actual = outputter.outputString(stateVersion3)
            val expected = readSourceTextFile(3, i)

            assertEquals("#" + i, expected, actual)
        }
    }

    @Test(expected = StudyUnrecognizedFormatException::class)
    fun convertToThirdVersionWithException() {
        val stateVersion2 = readXmlFile(StudySerializationUtilsTest::class.java, "version2_0.xml")
        SerializationUtils.convertToThirdVersion(stateVersion2)
    }

    @Test
    fun convertToFourthVersion() {
        for (i in 1..SOURCES_COUNT) {
            val stateVersion3 = readSourceXmlFile(3, i)
            val stateVersion4 = SerializationUtils.convertToFourthVersion(stateVersion3)

            val xs = xStream
            xs.alias("StepikProjectManager", StepikProjectManager::class.java)

            val obj = xs.fromXML(outputter.outputString(stateVersion4.getChild(SerializationUtils.MAIN_ELEMENT)))

            val actual = xs.toXML(obj)
            val expected = readSourceTextFile(4, i)

            assertEquals("#" + i, expected, actual)
        }
    }

    companion object {
        private const val SOURCES_COUNT = 4
        private lateinit var outputter: XMLOutputter

        @BeforeClass
        @JvmStatic
        fun before() {
            outputter = XMLOutputter()
            val format = Format.getPrettyFormat().setLineSeparator("\n")
            outputter.format = format
        }

        private fun readSourceXmlFile(version: Int, index: Int): Element {
            return readXmlFile(StudySerializationUtilsTest::class.java, format("version{0}_{1}.xml", version, index))
        }

        private fun readSourceTextFile(version: Int, index: Int): String {
            return readTextFile(StudySerializationUtilsTest::class.java, format("version{0}_{1}.xml", version, index))
        }
    }
}
