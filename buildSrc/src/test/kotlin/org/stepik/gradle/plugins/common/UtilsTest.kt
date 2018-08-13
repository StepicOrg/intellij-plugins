package org.stepik.gradle.plugins.common

import org.jdom2.Document
import org.jdom2.Element
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.stepik.gradle.plugins.common.Utils.APPLICATION
import org.stepik.gradle.plugins.common.Utils.COMPONENT
import org.stepik.gradle.plugins.common.Utils.IdeXml
import org.stepik.gradle.plugins.common.Utils.NAME
import org.stepik.gradle.plugins.common.Utils.VALUE

class UtilsTest {
    companion object {
        private const val IDEA_PLUGIN = "idea-plugin"
        private const val IDEA_VERSION = "idea-version"
        private const val SINCE_BUILD = "since-build"
        private const val VALID_IDE_VERSION = "163.8344"
    }
    
    private var ideaVersionTag: Element? = null
    
    @Before
    fun setUp() {
        val document = Document()
        val root = Element(IDEA_PLUGIN)
        document.rootElement = root
        ideaVersionTag = Element(IDEA_VERSION)
        root.addContent(ideaVersionTag)
    }
    
    @Test
    fun setAttributeValue() {
        Utils.setAttributeValue(ideaVersionTag!!, SINCE_BUILD, VALID_IDE_VERSION)
        
        assertEquals(ideaVersionTag!!.getAttributeValue(SINCE_BUILD), VALID_IDE_VERSION)
    }
    
    @Test
    fun createUpdatesXml() {
        val xml = Utils.createXml(Utils.UPDATE_XML)
        
        checkUpdateXml(xml, Utils.UPDATE_XML)
    }
    
    @Test
    fun repairUpdatesXml() {
        val xml = Document()
        
        Utils.repairXml(xml, Utils.UPDATE_XML)
        
        checkUpdateXml(xml, Utils.UPDATE_XML)
    }
    
    @Test
    fun createIdeGeneralXml() {
        val xml = Utils.createXml(Utils.IDE_GENERAL_XML)
        
        checkUpdateXml(xml, Utils.IDE_GENERAL_XML)
    }
    
    @Test
    fun repairIdeGeneralXml() {
        val xml = Document()
        
        Utils.repairXml(xml, Utils.IDE_GENERAL_XML)
        
        checkUpdateXml(xml, Utils.IDE_GENERAL_XML)
    }
    
    @Test
    fun createOptionsXml() {
        val xml = Utils.createXml(Utils.OPTIONS_XML)
        
        checkUpdateXml(xml, Utils.OPTIONS_XML)
    }
    
    @Test
    fun repairOptionsXml() {
        val xml = Document()
        
        Utils.repairXml(xml, Utils.OPTIONS_XML)
        
        checkUpdateXml(xml, Utils.OPTIONS_XML)
    }
    
    private fun checkUpdateXml(xml: Document, map: IdeXml) {
        val root = xml.rootElement
        assertEquals(root.name, APPLICATION)
        val component = root.getChild(COMPONENT)
        assertNotNull(component)
        assertEquals(component.getAttributeValue(NAME), map.componentName)
        
        val optionTags = component.getChildren(map.optionTag)
        val optionsMap = optionTags.map {
            it.getAttributeValue(NAME) to it
        }
                .toMap()
        
        map.options.forEach {
            val optionTag = optionsMap[it.key]
            
            assertNotNull(optionTag)
            assertEquals(optionTag!!.getAttributeValue(VALUE), it.value)
        }
    }
}
