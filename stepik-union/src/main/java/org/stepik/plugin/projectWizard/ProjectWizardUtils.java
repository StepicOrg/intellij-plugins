package org.stepik.plugin.projectWizard;

import java.io.File;

/**
 * @author meanmail
 */
public class ProjectWizardUtils {
    public static String findNonExistingFileName(String searchDirectory, String preferredName) {
        String fileName = preferredName;
        int idx = 1;

        while (new File(searchDirectory, fileName).exists()) {
            fileName = preferredName + "_" + idx;
            ++idx;
        }

        return fileName;
    }
}
