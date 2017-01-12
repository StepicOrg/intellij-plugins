package com.jetbrains.tmp.learning.courseFormat;

import java.util.Comparator;

/**
 * @author meanmail
 */
class StudyNodeComparator implements Comparator<StudyNode> {

    private static Comparator<? super SectionNode> instance;

    public static Comparator<? super SectionNode> getInstance() {
        if (instance == null) {
            instance = new StudyNodeComparator();
        }
        return instance;
    }

    @Override
    public int compare(StudyNode item1, StudyNode item2) {
        return Integer.compare(item1.getPosition(), item2.getPosition());
    }
}
