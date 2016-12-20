//package org.stepik.plugin.projectWizard.pycharm;
//
//import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
//import com.jetbrains.tmp.learning.stepik.StepikWrappers;
//import org.jetbrains.annotations.NotNull;
//
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.Arrays;
//import java.util.List;
//
//class CheckCourseLinkListener implements ActionListener {
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        String link = courseLinkFiled.getText();
//        String courseId = getCourseIdFromLink(link);
//
//        StepikWrappers.CoursesContainer coursesContainer;
//        if ("-1".equals(courseId) ||
//                (coursesContainer = StepikConnectorGet.getCourseInfos(courseId)) == null) {
//            courseLinkDescription.setText("Wrong link");
//            return;
//        }
//
//        selectedCourse = coursesContainer.courses.get(0);
//        generator.setSelectedCourse(selectedCourse);
//        courseLinkDescription.setText(String.format("<b>Course:</b> %s<br><br>%s",
//                selectedCourse.toString(), selectedCourse.getDescription()));
//    }
//
//    @NotNull
//    private String getCourseIdFromLink(@NotNull String link) {
//        link = link.trim();
//        if (link.isEmpty()) {
//            return "-1";
//        }
//        if (isFillOfInt(link)) {
//            return link;
//        }
//
//        List<String> list = Arrays.asList(link.split("/"));
//        int i = list.indexOf("course");
//        if (i != -1) {
//            if (i + 1 == list.size())
//                return "-1";
//            String[] parts = list.get(i + 1).split("-");
//            return parts.length != 0 ? parts[parts.length - 1] : "-1";
//        }
//
//        String[] paramStr = link.split("\\?");
//        if (paramStr.length > 1) {
//            String[] params = paramStr[1].split("&");
//            final String[] unitId = {"-1"};
//            Arrays.stream(params)
//                    .filter(s -> s.startsWith("unit="))
//                    .forEach(s -> unitId[0] = s.substring(5, s.length()));
//
//            if (!unitId[0].equals("-1")) {
//                StepikWrappers.UnitContainer unitContainer =
//                        StepikConnectorGet.getUnits(unitId[0]);
//                if (unitContainer == null) {
//                    return "-1";
//                }
//                return getCourseId(unitContainer);
//            }
//        }
//
//        list = Arrays.asList(link.split("/"));
//        i = list.indexOf("lesson");
//        if (i != -1) {
//            if (i + 1 == list.size())
//                return "-1";
//            String[] parts = list.get(i + 1).split("-");
//            String lessonId = parts[parts.length - 1];
//            StepikWrappers.UnitContainer unitContainer =
//                    StepikConnectorGet.getUnits("?lesson=" + lessonId);
//
//            return unitContainer == null ? "-1" : getCourseId(unitContainer);
//        }
//        return "-1";
//    }
//
//    @NotNull
//    private String getCourseId(@NotNull StepikWrappers.UnitContainer unitContainer) {
//        String sectionId = Integer.toString(unitContainer.units.get(0).section);
//        StepikWrappers.SectionContainer sectionContainer =
//                StepikConnectorGet.getSections(sectionId);
//
//        return sectionContainer == null ? "-1" :
//                Integer.toString(sectionContainer.sections.get(0).course);
//    }
//
//    private boolean isFillOfInt(@NotNull String link) {
//        return link.matches("[0-9]+");
//    }
//}