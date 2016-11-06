package utils;

import org.jetbrains.annotations.NotNull;

import static org.stepik.plugin.utils.ProjectFilesUtils.SEPARATOR;

/**
 * @author meanmail
 */
public class TestUtils {
    @NotNull
    public static String join(@NotNull CharSequence... elements) {
        return String.join(SEPARATOR, elements);
    }
}
