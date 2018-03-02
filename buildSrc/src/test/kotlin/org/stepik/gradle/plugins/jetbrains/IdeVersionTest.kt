package org.stepik.gradle.plugins.jetbrains

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith


@RunWith(Theories::class)
class IdeVersionTest {

    @Theory
    fun fromStringValid(@FromDataPoints("validVersions") version: Array<String>) {
        val ideVersion = IdeVersion.fromString(version[0])

        assertNotNull(ideVersion)

        assertEquals(version[1], ideVersion?.baselineVersion.toString())
        assertEquals(version[2], ideVersion?.build.toString())
    }

    @Theory
    fun fromStringNotValid(@FromDataPoints("notValidVersions") version: String) {
        val ideVersion = IdeVersion.fromString(version)

        assertNull(ideVersion)
    }

    companion object {
        @DataPoints("validVersions")
        @JvmField
        val validVersions = arrayOf(
                arrayOf("163.8444.3", "163", "8444"),
                arrayOf("162", "162", "0"),
                arrayOf("CE-173.2588.8", "173", "2588")
        )

        @DataPoints("notValidVersions")
        @JvmField
        val notValidVersions = arrayOf(
                "IC",
                "",
                ".888",
                "..",
                "CE-.25",
                "CE-..25"
        )
    }
}
