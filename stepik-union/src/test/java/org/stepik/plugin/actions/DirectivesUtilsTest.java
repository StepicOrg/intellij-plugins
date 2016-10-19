package org.stepik.plugin.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.stepik.plugin.collective.SupportedLanguages;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DirectivesUtilsTest {
    final String JAVA = "java" + File.separator;
    final String PYTHON = "python" + File.separator;
    private HashMap<String, String> testsMap = new HashMap<>();

    public String readTestFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = this.getClass().getResourceAsStream(File.separator + "samples" + File.separator + fileName);
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append("\n");
        }

        return sb.toString();
    }

    @Before
    public void setUp() throws Exception {
        for (int i = 1; i < 7; i++) {
            String fileName = JAVA + Integer.toString(i) + ".txt";
            testsMap.put(JAVA + i, readTestFile(fileName));
        }

        for (int i = 1; i < 7; i++) {
            String fileName = PYTHON + Integer.toString(i) + ".txt";
            testsMap.put(PYTHON + i, readTestFile(fileName));
        }
    }

    @Test
    public void getTextUnderDirectivesJava() throws Exception {
        Map<Integer, String> res = new HashMap<>();
        for (int i = 1; i < 7; i++) {
            String test = testsMap.get(JAVA + i);
            res.put(i, DirectivesUtils.getTextUnderDirectives(test.split("\n"), SupportedLanguages.JAVA));
            Assert.assertEquals(testsMap.get(JAVA + "1"), res.get(i));
            System.out.println("java test " + i + " passed");
        }

    }

    @Test
    public void getTextUnderDirectivesPy() throws Exception {
        Map<Integer, String> res = new HashMap<>();
        for (int i = 1; i < 7; i++) {
            String test = testsMap.get(PYTHON + i);
            res.put(i, DirectivesUtils.getTextUnderDirectives(test.split("\n"), SupportedLanguages.PYTHON));
            Assert.assertEquals(testsMap.get(PYTHON + 1), res.get(i));
            System.out.println("python test " + i + " passed");
        }

    }
}