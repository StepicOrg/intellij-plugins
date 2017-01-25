package org.stepik.plugin.projectWizard;

import java.io.File;

/**
 * @author meanmail
 */
public class ProjectWizardUtils {
    public static String findNonExistingFileName(String searchDirectory, String preferredName) {
        int idx = 0;

        while (true) {
            String fileName = (idx > 0 ? preferredName + "_" + idx : preferredName);
            if (!(new File(searchDirectory, fileName)).exists()) {
                return fileName;
            }

            ++idx;
        }
    }
}
