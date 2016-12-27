package com.jetbrains.tmp.learning.courseFormat;

import java.util.Comparator;

/**
 * @author meanmail
 */
class StudyItemComparator implements Comparator<StudyItem> {

    private static Comparator<? super Section> instance;

    public static Comparator<? super Section> getInstance() {
        if (instance == null) {
            instance = new StudyItemComparator();
        }
        return instance;
    }

    @Override
    public int compare(StudyItem item1, StudyItem item2) {
        return Integer.compare(item1.getPosition(), item2.getPosition());
    }
}
