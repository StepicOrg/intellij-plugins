package org.stepik.plugin.actions;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.stepik.plugin.collective.SupportedLanguages;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DirectivesUtilsTest {
    private static final String NOT_FOUND = "Test file not found: ";
    private static final String JAVA = "java";
    private static final String PYTHON = "python";
    private final HashMap<String, String> testsMap = new HashMap<>();

    private String readTestFile(@NotNull String fileName) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = this.getClass().getResourceAsStream("/samples/" + fileName);

        if (inputStream == null)
            return null;
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append("\n");
        }

        return sb.toString();
    }

    @Before
    public void setUp() throws Exception {
        for (int i = 1; i < 7; i++) {
            String fileName = JAVA + "/" + Integer.toString(i) + ".txt";
            testsMap.put(JAVA + i, readTestFile(fileName));
        }

        for (int i = 1; i < 7; i++) {
            String fileName = PYTHON + "/" + Integer.toString(i) + ".txt";
            testsMap.put(PYTHON + i, readTestFile(fileName));
        }
    }

    @Test
    public void getTextUnderDirectivesJava() throws Exception {
        Map<Integer, String> res = new HashMap<>();
        for (int i = 1; i < 7; i++) {
            String testName = JAVA + i;
            String test = testsMap.get(testName);
            Assert.assertNotNull(NOT_FOUND + testName, test);
            res.put(i, DirectivesUtils.getTextUnderDirectives(test.split("\n"), SupportedLanguages.JAVA));
            Assert.assertEquals(testsMap.get(JAVA + 1), res.get(i));
        }

    }

    @Test
    public void getTextUnderDirectivesPy() throws Exception {
        Map<Integer, String> res = new HashMap<>();
        for (int i = 1; i < 7; i++) {
            String testName = PYTHON + i;
            String test = testsMap.get(testName);
            Assert.assertNotNull(NOT_FOUND + testName, test);
            res.put(i, DirectivesUtils.getTextUnderDirectives(test.split("\n"), SupportedLanguages.PYTHON));
            Assert.assertEquals(testsMap.get(PYTHON + 1), res.get(i));
        }

    }
}