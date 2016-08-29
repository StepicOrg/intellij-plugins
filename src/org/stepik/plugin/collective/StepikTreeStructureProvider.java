package org.stepik.plugin.collective;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.jetbrains.edu.utils.EduTreeStructureProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class StepikTreeStructureProvider extends EduTreeStructureProvider {
    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent, @NotNull Collection<AbstractTreeNode> children, ViewSettings settings) {
        return children;
    }
}
