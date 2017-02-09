package org.stepik.plugin.actions.navigation;

import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class StudyNavigator {
    private StudyNavigator() {
    }

    @Nullable
    private static StudyNode navigate(
            @Nullable final StudyNode prevNode,
            @Nullable final StudyNode currentNode,
            @NotNull Direction direction) {
        if (currentNode == null) {
            return null;
        }

        StudyNode parent = currentNode.getParent();

        if (currentNode.isLeaf()) {
            if (parent != null) {
                switch (direction) {
                    case BACK:
                        return parent.getPrevChild(currentNode);
                    case FORWARD:
                        return parent.getNextChild(currentNode);
                }
            } else {
                return null;
            }
        } else {
            StudyNode targetNode = null;
            switch (direction) {
                case BACK:
                    targetNode = currentNode.getPrevChild(prevNode);
                    break;
                case FORWARD:
                    targetNode = currentNode.getNextChild(prevNode);
                    break;
            }

            if (targetNode != null) {
                return navigate(null, targetNode, direction);
            } else if (parent != null) {
                return navigate(currentNode, parent, direction);
            }
        }
        return null;
    }

    @Nullable
    static StudyNode nextLeaf(@Nullable final StudyNode node) {
        return navigate(null, node, Direction.FORWARD);
    }

    @Nullable
    static StudyNode previousLeaf(@Nullable final StudyNode node) {
        return navigate(null, node, Direction.BACK);
    }

    private enum Direction {
        BACK, FORWARD
    }
}
