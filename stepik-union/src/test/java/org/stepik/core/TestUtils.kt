package org.stepik.core

import org.jdom.Element
import org.jdom.input.DOMBuilder
import org.junit.Assert.assertNotNull
import org.stepik.core.utils.ProjectFilesUtils.SEPARATOR
import org.xml.sax.SAXException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException


object TestUtils {
    fun join(vararg elements: CharSequence): String {
        return elements.joinToString(SEPARATOR)
    }

    @Throws(ParserConfigurationException::class, SAXException::class, IOException::class)
    fun readXmlFile(clazz: Class<*>, filename: String): Element {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isValidating = false
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(clazz.getResourceAsStream(filename))
        val domBuilder = DOMBuilder()

        val root = domBuilder.build(doc).rootElement
        assertNotNull("Failed read test xml data file", root)

        return root
    }

    @Throws(IOException::class)
    fun readTextFile(clazz: Class<*>, filename: String): String {
        return BufferedReader(InputStreamReader(
                clazz.getResourceAsStream(filename),
                StandardCharsets.UTF_8)).use {
            it.lines().collect(Collectors.joining("\n"))
        }
    }
}
