package org.stepik.core.courseFormat

import java.util.*


internal class StudyNodeComparator : Comparator<StudyNode<*, *>> {

    override fun compare(item1: StudyNode<*, *>, item2: StudyNode<*, *>): Int {
        return Integer.compare(item1.position, item2.position)
    }

    companion object {

        val instance: Comparator<in StudyNode<*, *>> by lazy {
            StudyNodeComparator()
        }
    }
}
