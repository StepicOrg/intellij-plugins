package org.stepik.core.actions.navigation

import org.stepik.core.courseFormat.StudyNode

object StudyNavigator {

    private fun navigate(
            prevNode: StudyNode?,
            currentNode: StudyNode?,
            direction: Direction): StudyNode? {

        currentNode ?: return null

        val parent = currentNode.parent

        if (currentNode.isLeaf) {
            if (parent == null) {
                return null
            }

            return when (direction) {
                Direction.BACK -> parent.getPrevChild(currentNode)
                Direction.FORWARD -> parent.getNextChild(currentNode)
            } ?: navigate(parent, parent.parent, direction)
        } else {
            val targetNode: StudyNode? = when (direction) {
                Direction.BACK -> currentNode.getPrevChild(prevNode)
                Direction.FORWARD -> currentNode.getNextChild(prevNode)
            }

            if (targetNode != null) {
                return if (targetNode.isLeaf) {
                    targetNode
                } else navigate(null, targetNode, direction)
            } else if (parent != null) {
                return navigate(currentNode, parent, direction)
            }
        }
        return null
    }

    fun nextLeaf(node: StudyNode?): StudyNode? {
        return navigate(null, node, Direction.FORWARD)
    }

    fun previousLeaf(node: StudyNode?): StudyNode? {
        return navigate(null, node, Direction.BACK)
    }

    private enum class Direction {
        BACK, FORWARD
    }
}
