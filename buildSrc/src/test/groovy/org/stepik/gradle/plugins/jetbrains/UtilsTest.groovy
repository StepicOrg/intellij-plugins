package org.stepik.gradle.plugins.jetbrains

import org.gradle.internal.Pair
import org.jdom2.Document
import org.jdom2.Element
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.stepik.gradle.plugins.jetbrains.Utils.APPLICATION
import static org.stepik.gradle.plugins.jetbrains.Utils.COMPONENT
import static org.stepik.gradle.plugins.jetbrains.Utils.COMPONENT_NAME
import static org.stepik.gradle.plugins.jetbrains.Utils.NAME
import static org.stepik.gradle.plugins.jetbrains.Utils.OPTIONS
import static org.stepik.gradle.plugins.jetbrains.Utils.OPTION_TAG
import static org.stepik.gradle.plugins.jetbrains.Utils.VALUE

/**
 * @author meanmail
 */
class UtilsTest {
    private static final String IDEA_PLUGIN = "idea-plugin"
    private static final String IDEA_VERSION = "idea-version"
    private static final String SINCE_BUILD = "since-build"
    private static final String VALID_IDE_VERSION = "163.8344"
    private Document document
    private Element ideaVersionTag

    @Before
    void setUp() {
        document = new Document()
        Element root = new Element(IDEA_PLUGIN)
        document.setRootElement(root)
        ideaVersionTag = new Element(IDEA_VERSION)
        root.addContent(ideaVersionTag)
    }

    @Test
    void setAttributeValue() {
        Utils.setAttributeValue(ideaVersionTag, SINCE_BUILD, VALID_IDE_VERSION)

        assertEquals(ideaVersionTag.getAttributeValue(SINCE_BUILD), VALID_IDE_VERSION)
    }

    @Test
    void createUpdatesXml() {
        Document xml = Utils.createXml(Utils.UPDATE_XML)

        checkUpdateXml(xml, Utils.UPDATE_XML)
    }

    @Test
    void repairUpdatesXml() {
        Document xml = new Document()

        Utils.repairXml(xml, Utils.UPDATE_XML)

        checkUpdateXml(xml, Utils.UPDATE_XML)
    }

    @Test
    void createIdeGeneralXml() {
        Document xml = Utils.createXml(Utils.IDE_GENERAL_XML)

        checkUpdateXml(xml, Utils.IDE_GENERAL_XML)
    }

    @Test
    void repairIdeGeneralXml() {
        Document xml = new Document()

        Utils.repairXml(xml, Utils.IDE_GENERAL_XML)

        checkUpdateXml(xml, Utils.IDE_GENERAL_XML)
    }

    @Test
    void createOptionsXml() {
        Document xml = Utils.createXml(Utils.OPTIONS_XML)

        checkUpdateXml(xml, Utils.OPTIONS_XML)
    }

    @Test
    void repairOptionsXml() {
        Document xml = new Document()

        Utils.repairXml(xml, Utils.OPTIONS_XML)

        checkUpdateXml(xml, Utils.OPTIONS_XML)
    }

    private static void checkUpdateXml(Document xml, Map map) {
        def root = xml.getRootElement()
        assertEquals(root.getName(), APPLICATION)
        def component = root.getChild(COMPONENT)
        assertNotNull(component)
        assertEquals(component.getAttributeValue(NAME), map[COMPONENT_NAME])

        def optionTags = component.getChildren(map[OPTION_TAG] as String)
        Map<String, Element> optionsMap = new HashMap<>()
        optionTags.each {
            optionsMap.put(it.getAttributeValue(NAME), it)
        }

        map[OPTIONS].each { Pair option ->
            def optionTag = optionsMap.get(option.getLeft())
            assertNotNull(optionTag)
            assertEquals(optionTag.getAttributeValue(VALUE), option.getRight())
        }
    }
}