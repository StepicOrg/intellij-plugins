package icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class AllStepikIcons {
    public static final Icon stepikLogo = load("/icons/stepikLogo.png"); // 16x16
    public static final Icon stepikLogoBig = load("/icons/stepikLogoBig.png"); // 24x24
    public static final Icon stepikLogoSmall = load("/icons/stepikLogoSmall.png"); // 13x13

    private static Icon load(String path) {
        return IconLoader.getIcon(path, AllStepikIcons.class);
    }

    public static class ProjectTree {
        public static final Icon course = load("/icons/projectTree/course.png"); // 16x16
        public static final Icon courseCorrect = load("/icons/projectTree/courseCorrect.png"); // 16x16
        public static final Icon module = load("/icons/projectTree/module.png"); // 16x16
        public static final Icon moduleCorrect = load("/icons/projectTree/moduleCorrect.png"); // 16x16
        public static final Icon lesson = load("/icons/projectTree/lesson.png"); // 16x16
        public static final Icon lessonCorrect = load("/icons/projectTree/lessonCorrect.png"); // 16x16
        public static final Icon stepCode = load("/icons/projectTree/stepCode.png"); // 16x16
        public static final Icon stepCodeCorrect = load("/icons/projectTree/stepCodeCorrect.png"); // 16x16
        public static final Icon stepProblem = load("/icons/projectTree/stepProblem.png"); // 16x16
        public static final Icon stepProblemCorrect = load("/icons/projectTree/stepProblemCorrect.png"); // 16x16
        public static final Icon stepText = load("/icons/projectTree/stepText.png"); // 16x16
        public static final Icon stepTextCorrect = load("/icons/projectTree/stepTextCorrect.png"); // 16x16
        public static final Icon stepVideo = load("/icons/projectTree/stepVideo.png"); // 16x16
        public static final Icon stepVideoCorrect = load("/icons/projectTree/stepVideoCorrect.png"); // 16x16
        public static final Icon sandbox = load("/icons/projectTree/sandbox.png"); // 16x16
    }

    public static class ToolWindow {
        public static final Icon checkTask = load("/icons/toolWindow/checkTask.png"); // 16x16
        public static final Icon resetTaskFile = load("/icons/toolWindow/resetTaskFile.png"); // 16x16
        public static final Icon download = AllIcons.Welcome.FromVCS; // 16x16
    }
}
