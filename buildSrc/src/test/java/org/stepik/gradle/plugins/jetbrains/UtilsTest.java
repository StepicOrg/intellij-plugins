package org.stepik.gradle.plugins.jetbrains;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author meanmail
 */
public class UtilsTest {

    private static final String IDEA_PLUGIN = "idea-plugin";
    private static final String IDEA_VERSION = "idea-version";
    private static final String SINCE_BUILD = "since-build";
    private static final String VALID_IDE_VERSION = "163.8344";
    private static final String APPLICATION = "application";
    private static final String COMPONENT = "component";
    private static final String NAME = "name";
    private static final String OPTION = "option";
    private static final String UPDATES_CONFIGURABLE = "UpdatesConfigurable";
    private static final String VALUE = "value";
    private static final String CHECK_NEEDED = "CHECK_NEEDED";
    private static final String FALSE = "false";
    private Document document;
    private Element ideaVersionTag;

    @Before
    public void setUp() {
        document = new Document();
        Element root = new Element(IDEA_PLUGIN);
        document.setRootElement(root);
        ideaVersionTag = new Element(IDEA_VERSION);
        root.addContent(ideaVersionTag);
    }

    @Test
    public void setAttributeValue() {
        Utils.setAttributeValue(ideaVersionTag, SINCE_BUILD, VALID_IDE_VERSION);
        assertEquals(ideaVersionTag.getAttributeValue(SINCE_BUILD), VALID_IDE_VERSION);
    }

    @Test
    public void createUpdatesXml() {
        Document updateXml = Utils.createUpdatesXml();

        checkUpdateXml(updateXml);
    }

    @Test
    public void repairUpdatesXml() {
        Document updateXml = new Document();

        Utils.repairUpdateXml(updateXml);

        checkUpdateXml(updateXml);
    }

    private void checkUpdateXml(Document updateXml) {
        Element root = updateXml.getRootElement();
        assertEquals(root.getName(), APPLICATION);
        Element component = root.getChild(COMPONENT);
        assertNotNull(component);
        assertEquals(component.getAttributeValue(NAME), UPDATES_CONFIGURABLE);
        Element option = component.getChild(OPTION);
        assertNotNull(option);
        assertEquals(option.getAttributeValue(NAME), CHECK_NEEDED);
        assertEquals(option.getAttributeValue(VALUE), FALSE);
    }
}