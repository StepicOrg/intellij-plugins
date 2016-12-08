package org.stepik.gradle.plugins.jetbrains

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith

import static org.junit.Assert.*

/**
 * @author meanmail
 */
@RunWith(Theories.class)
class IdeVersionTest {

    @DataPoints("validVersions")
    public static final String[][] validVersions = [
            ["163.8444.3", "163", "8444"],
            ["162", "162", "0"],
            ["CE-173.2588.8", "173", "2588"]
    ]

    @DataPoints("notValidVersions")
    public static final String[] notValidVersions = [
            "IC",
            "",
            ".888",
            "..",
            "CE-.25",
            "CE-..25"
    ]

    @Theory
    void fromStringValid(@FromDataPoints("validVersions") String[] version) throws Exception {
        IdeVersion ideVersion = IdeVersion.fromString(version[0])

        assertNotNull(ideVersion)

        assertEquals(version[1], String.valueOf(ideVersion.getBaselineVersion()))
        assertEquals(version[2], String.valueOf(ideVersion.getBuild()))
    }

    @Theory
    void fromStringNotValid(@FromDataPoints("notValidVersions") String version) throws Exception {
        IdeVersion ideVersion = IdeVersion.fromString(version)

        assertNull(ideVersion)
    }
}