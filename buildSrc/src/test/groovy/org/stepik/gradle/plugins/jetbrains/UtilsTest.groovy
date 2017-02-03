package org.stepik.gradle.plugins.jetbrains

import org.gradle.internal.Pair
import org.jdom2.Document
import org.jdom2.Element
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 * @author meanmail
 */
class UtilsTest {
    private static final String IDEA_PLUGIN = "idea-plugin"
    private static final String IDEA_VERSION = "idea-version"
    private static final String SINCE_BUILD = "since-build"
    private static final String VALID_IDE_VERSION = "163.8344"
    private static final String APPLICATION = "application"
    private static final String COMPONENT = "component"
    private static final String NAME = "name"
    private static final String VALUE = "value"
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
        assertEquals(component.getAttributeValue(NAME), map["componentName"])

        def optionTags = component.getChildren(map["optionTag"] as String)
        Map<String, Element> optionsMap = new HashMap<>()
        optionTags.each {
            optionsMap.put(it.getAttributeValue(NAME), it)
        }

        map["options"].each { Pair option ->
            def optionTag = optionsMap.get(option.getLeft())
            assertNotNull(optionTag)
            assertEquals(optionTag.getAttributeValue(VALUE), option.getRight())
        }
    }
}