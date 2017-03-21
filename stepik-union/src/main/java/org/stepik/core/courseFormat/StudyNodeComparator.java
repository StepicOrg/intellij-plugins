package org.stepik.core.courseFormat;

import java.util.Comparator;

/**
 * @author meanmail
 */
class StudyNodeComparator implements Comparator<StudyNode> {

    private static Comparator<? super StudyNode> instance;

    public static Comparator<? super StudyNode> getInstance() {
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
