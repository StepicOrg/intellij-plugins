package org.stepik.plugin.projectView;

import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class PyCharmTreeStructureProvider extends StepikTreeStructureProvider {
    @Override
    protected boolean shouldAdd(@NotNull Object object) {
        return false;
    }
}