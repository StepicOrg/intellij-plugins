package org.stepik.plugin.utils

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.stepik.core.SupportedLanguages
import java.util.*

class DirectivesUtilsKtTest {
    companion object {
        private val NOT_FOUND = "Test file not found: "
        private val JAVA = "java"
        private val PYTHON = "python"
        private val TESTS_COUNT = 7
    }

    private val sourcesMap = HashMap<String, String>()
    private val expectedMap = HashMap<String, String>()
    private val expectedReplacedMap = HashMap<String, String>()

    private fun readTestFile(fileName: String): String {
        val inputStream = this.javaClass.getResourceAsStream("/samples/" + fileName) ?: return ""

        val scanner = Scanner(inputStream)
        val lines = ArrayList<String>()

        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine())
        }

        return lines.joinToString("\n")
    }

    @Before
    fun setUp() {
        loadResources(JAVA, ".java")
        loadResources(PYTHON, ".py")
    }

    private fun loadResources(language: String, extension: String) {
        for (i in 1..TESTS_COUNT) {
            val fileName = language + "/sources/" + Integer.toString(i) + extension
            sourcesMap.put(language + i, readTestFile(fileName))
        }
        for (i in 1..TESTS_COUNT) {
            val fileName = language + "/expected/" + Integer.toString(i) + extension
            expectedMap.put(language + i, readTestFile(fileName))
        }
        for (i in 1..TESTS_COUNT) {
            val fileName = language + "/expected_replaced/" + Integer.toString(i) + extension
            expectedReplacedMap.put(language + i, readTestFile(fileName))
        }
    }

    @Test
    fun getTextUnderDirectivesJava() {
        for (i in 1..TESTS_COUNT) {
            val testName = JAVA + i
            val test = sourcesMap[testName]
            Assert.assertNotNull(NOT_FOUND + testName, test)
            val actual = getTextUnderDirectives(test ?: "", SupportedLanguages.JAVA8)
            Assert.assertEquals(expectedMap[JAVA + i], actual)
        }
    }

    @Test
    fun getTextUnderDirectivesPy() {
        for (i in 1..TESTS_COUNT) {
            val testName = PYTHON + i
            val test = sourcesMap[testName]
            Assert.assertNotNull(NOT_FOUND + testName, test)
            val actual = getTextUnderDirectives(test ?: "", SupportedLanguages.PYTHON3)
            Assert.assertEquals(expectedMap[PYTHON + i], actual)
        }
    }

    @Test
    fun removeAmbientCodeJava() {
        val actual = removeAmbientCode(sourcesMap[JAVA + 7] ?: "", false, SupportedLanguages.JAVA8, false)
        Assert.assertEquals(sourcesMap[JAVA + 1], actual)
    }

    @Test
    fun removeAmbientCodePy() {
        val actual = removeAmbientCode(sourcesMap[PYTHON + 7] ?: "", false, SupportedLanguages.PYTHON3, false)
        Assert.assertEquals(sourcesMap[PYTHON + 1], actual)
    }

    @Test
    fun insertAmbientCodeJava() {
        val actual = insertAmbientCode(sourcesMap[JAVA + 1] ?: "", SupportedLanguages.JAVA8, false)
        Assert.assertEquals(sourcesMap[JAVA + 7], actual)
    }

    @Test
    fun insertAmbientCodePy() {
        val actual = insertAmbientCode(sourcesMap[PYTHON + 1] ?: "", SupportedLanguages.PYTHON3, false)
        Assert.assertEquals(sourcesMap[PYTHON + 7], actual)
    }

    @Test
    fun replaceCodeJava() {
        for (i in 1..TESTS_COUNT) {
            val testName = JAVA + i
            val test = sourcesMap[testName]
            Assert.assertNotNull(NOT_FOUND + testName, test)
            val actual = replaceCode(test ?: "", "replaced", SupportedLanguages.JAVA8)
            Assert.assertEquals(expectedReplacedMap[JAVA + i], actual)
        }
    }

    @Test
    fun replaceCodePy() {
        for (i in 1..TESTS_COUNT) {
            val testName = PYTHON + i
            val test = sourcesMap[testName]
            Assert.assertNotNull(NOT_FOUND + testName, test)
            val actual = replaceCode(test ?: "", "replaced", SupportedLanguages.PYTHON3)
            Assert.assertEquals(expectedReplacedMap[PYTHON + i], actual)
        }
    }

    @Test
    fun uncommentAmbientCodeJava() {
        for (i in 1..TESTS_COUNT) {
            val testName = JAVA + i
            val test = expectedMap[testName]
            Assert.assertNotNull(NOT_FOUND + testName, test)
            val actual = uncommentAmbientCode(test ?: "", SupportedLanguages.JAVA8)
            Assert.assertEquals(sourcesMap[JAVA + i], actual)
        }
    }

    @Test
    fun uncommentAmbientCodePy() {
        for (i in 1..TESTS_COUNT) {
            val testName = PYTHON + i
            val test = expectedMap[testName]
            Assert.assertNotNull(NOT_FOUND + testName, test)
            val actual = uncommentAmbientCode(test ?: "", SupportedLanguages.PYTHON3)
            Assert.assertEquals(sourcesMap[PYTHON + i], actual)
        }
    }

    @Test
    fun containsDirectivesJava() {
        for (i in 2..TESTS_COUNT) {
            val testName = JAVA + i
            val test = expectedMap[testName]
            Assert.assertNotNull(NOT_FOUND + testName, test)
            val actual = containsDirectives(test ?: "", SupportedLanguages.JAVA8)
            Assert.assertTrue(actual)
        }
    }

    @Test
    fun notContainsDirectivesJava() {
        val testName = JAVA + 1
        val test = expectedMap[testName]
        Assert.assertNotNull(NOT_FOUND + testName, test)
        val actual = containsDirectives(test ?: "", SupportedLanguages.JAVA8)
        Assert.assertFalse(actual)
    }

    @Test
    fun containsDirectivesPy() {
        for (i in 2..TESTS_COUNT) {
            val testName = PYTHON + i
            val test = expectedMap[testName]
            Assert.assertNotNull(NOT_FOUND + testName, test)
            val actual = containsDirectives(test ?: "", SupportedLanguages.PYTHON3)
            Assert.assertTrue(actual)
        }
    }

    @Test
    fun notContainsDirectivesPy() {
        val testName = PYTHON + 1
        val test = expectedMap[testName]
        Assert.assertNotNull(NOT_FOUND + testName, test)
        val actual = containsDirectives(test ?: "", SupportedLanguages.PYTHON3)
        Assert.assertFalse(actual)
    }
}