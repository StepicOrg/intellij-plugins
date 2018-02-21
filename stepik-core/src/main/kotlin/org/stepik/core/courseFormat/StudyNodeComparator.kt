package org.stepik.core.courseFormat

import java.util.*


internal object StudyNodeComparator : Comparator<StudyNode> {

    override fun compare(item1: StudyNode, item2: StudyNode): Int {
        return item1.position.compareTo(item2.position)
    }
}
