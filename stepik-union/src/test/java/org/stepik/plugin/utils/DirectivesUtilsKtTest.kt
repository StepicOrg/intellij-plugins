package org.stepik.plugin.utils

import org.junit.Assert
import org.junit.Before
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.stepik.core.SupportedLanguages
import java.util.*


@RunWith(Theories::class)
class DirectivesUtilsKtTest {
    companion object {
        private val TESTS_COUNT = 7
        private val SOURCE = 0
        private val EXPECTED = 1
        private val EXPECTED_REPLACED = 2

        @DataPoints("languages")
        @JvmField val languages = listOf(SupportedLanguages.JAVA8, SupportedLanguages.PYTHON3)
    }

    private val sourcesMap = HashMap<SupportedLanguages, List<List<String>>>()

    private fun readTestFile(fileName: String): String {
        val inputStream = this::class.java.getResourceAsStream("/samples/" + fileName)

        Assert.assertNotNull(inputStream)

        val scanner = Scanner(inputStream)
        val lines = ArrayList<String>()

        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine())
        }

        return lines.joinToString("\n")
    }

    @Before
    fun setUp() {
        loadResources(SupportedLanguages.JAVA8, ".java")
        loadResources(SupportedLanguages.PYTHON3, ".py")
    }

    private fun loadResources(language: SupportedLanguages, extension: String) {
        val sources = (1..TESTS_COUNT).map {
            listOf(
                    readTestFile("${language.langName}/sources/$it$extension"),
                    readTestFile("${language.langName}/expected/$it$extension"),
                    readTestFile("${language.langName}/expected_replaced/$it$extension")
            )
        }

        sourcesMap.put(language, sources)
    }

    @Theory
    fun getTextUnderDirectives(@FromDataPoints("languages") language: SupportedLanguages) {
        val sources: List<List<String>> = sourcesMap[language]!!

        for ((index, value) in sources.withIndex()) {
            val actual = getTextUnderDirectives(value[SOURCE], language)
            val expected = value[EXPECTED]
            Assert.assertEquals("$language: $index", expected, actual)
        }
    }

    @Theory
    fun removeAmbientCode(@FromDataPoints("languages") language: SupportedLanguages) {
        val sources = sourcesMap[language]!!

        val actual = removeAmbientCode(sources[6][SOURCE], false, language, false)
        Assert.assertEquals(sources[0][SOURCE], actual)
    }

    @Theory
    fun insertAmbientCode(@FromDataPoints("languages") language: SupportedLanguages) {
        val sources = sourcesMap[language]!!
        val actual = insertAmbientCode(sources[0][SOURCE], language, false)
        Assert.assertEquals(sources[6][SOURCE], actual)
    }

    @Theory
    fun replaceCode(@FromDataPoints("languages") language: SupportedLanguages) {
        val files = sourcesMap[language]!!

        for ((index, value) in files.withIndex()) {
            val actual = replaceCode(value[SOURCE], "replaced", language)
            Assert.assertEquals("$language: $index", value[EXPECTED_REPLACED], actual)
        }
    }

    @Theory
    fun uncommentAmbientCode(@FromDataPoints("languages") language: SupportedLanguages) {
        val files = sourcesMap[language]!!

        for ((index, value) in files.withIndex()) {
            val actual = uncommentAmbientCode(value[EXPECTED], language)
            Assert.assertEquals("$language: $index", value[SOURCE], actual)
        }
    }

    @Theory
    fun containsDirectivesJava(@FromDataPoints("languages") language: SupportedLanguages) {
        val files = sourcesMap[language]!!

        for (index in 1..TESTS_COUNT - 1) {
            val actual = containsDirectives(files[index][EXPECTED], language)
            Assert.assertTrue("$language: $index", actual)
        }
    }

    @Theory
    fun notContainsDirectives(@FromDataPoints("languages") language: SupportedLanguages) {
        val files = sourcesMap[language]!!

        val actual = containsDirectives(files[0][EXPECTED], language)
        Assert.assertFalse(actual)
    }
}
