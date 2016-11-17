package org.stepik.core;

import org.jetbrains.annotations.NotNull;

import static org.stepik.core.utils.ProjectFilesUtils.SEPARATOR;

/**
 * @author meanmail
 */
public class TestUtils {
    @NotNull
    public static String join(@NotNull CharSequence... elements) {
        return String.join(SEPARATOR, elements);
    }
}
