package org.stepik.plugin.projectView.pycharm;

import org.jetbrains.annotations.NotNull;
import org.stepik.plugin.projectView.StepikTreeStructureProvider;

/**
 * @author meanmail
 */
public class PyCharmTreeStructureProvider extends StepikTreeStructureProvider {
    @Override
    protected boolean shouldAdd(@NotNull Object object) {
        return false;
    }
}