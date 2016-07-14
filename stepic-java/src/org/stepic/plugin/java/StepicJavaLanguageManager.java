package org.stepic.plugin.java;

import com.jetbrains.edu.learning.StudyLanguageManager;
import org.jetbrains.annotations.NotNull;

public class StepicJavaLanguageManager implements StudyLanguageManager {
    @NotNull
    @Override
    public String getTestFileName() {
        return "tests.java";
    }

    @NotNull
    @Override
    public String getTestHelperFileName() {
        //TODO: allow nullable
        return "no test_helper";
    }

    @NotNull
    @Override
    public String getUserTester() {
        return "userTester.java";
    }
}
