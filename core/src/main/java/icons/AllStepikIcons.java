package icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class AllStepikIcons {
    private static Icon load(String path) {
        return IconLoader.getIcon(path, AllStepikIcons.class);
    }

    public static final Icon stepikLogo = load("/icons/stepikLogo.png"); // 16x16
    public static final Icon stepikLogoSmall = load("/icons/stepikLogoSmall.png"); // 13x13

    public static class ProjectTree {
        public static final Icon course = load("/icons/projectTree/course.png"); // 16x16
        public static final Icon courseCorrect = load("/icons/projectTree/courseCorrect.png"); // 16x16
        public static final Icon courseWrong = load("/icons/projectTree/courseWrong.png"); // 16x16
        public static final Icon module = load("/icons/projectTree/module.png"); // 16x16
        public static final Icon moduleCorrect = load("/icons/projectTree/moduleCorrect.png"); // 16x16
        public static final Icon moduleWrong = load("/icons/projectTree/moduleWrong.png"); // 16x16
        public static final Icon lesson = load("/icons/projectTree/lesson.png"); // 16x16
        public static final Icon lessonCorrect = load("/icons/projectTree/lessonCorrect.png"); // 16x16
        public static final Icon lessonWrong = load("/icons/projectTree/lessonWrong.png"); // 16x16
        public static final Icon step = load("/icons/projectTree/step.png"); // 16x16
        public static final Icon stepCorrect = load("/icons/projectTree/stepCorrect.png"); // 16x16
        public static final Icon stepWrong = load("/icons/projectTree/stepWrong.png"); // 16x16
        public static final Icon sandbox = load("/icons/projectTree/sandbox.png"); // 16x16
    }

    public static class ToolWindow {
        public static final Icon checkTask = load("/icons/toolWindow/checkTask.png"); // 16x16
        public static final Icon resetTaskFile = load("/icons/toolWindow/resetTaskFile.png"); // 16x16
        public static final Icon download = AllIcons.Welcome.FromVCS; // 16x16
        public static final Icon taskDescription = load("/icons/toolWindow/taskDescription.png"); // 13x13
    }
}
