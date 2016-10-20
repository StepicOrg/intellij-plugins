package org.stepik.plugin.java;

import com.jetbrains.tmp.learning.StudyLanguageManager;
import org.jetbrains.annotations.NotNull;

public class StepikJavaLanguageManager implements StudyLanguageManager {
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
