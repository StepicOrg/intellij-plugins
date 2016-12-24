package icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class InteractiveLearningIcons {
    private static Icon load(String path) {
        return IconLoader.getIcon(path, InteractiveLearningIcons.class);
    }

    public static final Icon Course = load("/icons/com/jetbrains/edu/learning/course.png"); // 16x16
    public static final Icon CourseCorrect = load("/icons/com/jetbrains/edu/learning/courseCorrect.png"); // 16x16
    public static final Icon CourseWrong = load("/icons/com/jetbrains/edu/learning/courseWrong.png"); // 16x16
    public static final Icon Module = load("/icons/com/jetbrains/edu/learning/module.png"); // 16x16
    public static final Icon ModuleCorrect = load("/icons/com/jetbrains/edu/learning/moduleCorrect.png"); // 16x16
    public static final Icon ModuleWrong = load("/icons/com/jetbrains/edu/learning/moduleWrong.png"); // 16x16
    public static final Icon Lesson = load("/icons/com/jetbrains/edu/learning/lesson.png"); // 16x16
    public static final Icon LessonCorrect = load("/icons/com/jetbrains/edu/learning/lessonCorrect.png"); // 16x16
    public static final Icon LessonWrong = load("/icons/com/jetbrains/edu/learning/lessonWrong.png"); // 16x16
    public static final Icon Step = load("/icons/com/jetbrains/edu/learning/step.png"); // 16x16
    public static final Icon StepCorrect = load("/icons/com/jetbrains/edu/learning/stepCorrect.png"); // 16x16
    public static final Icon StepWrong = load("/icons/com/jetbrains/edu/learning/stepWrong.png"); // 16x16

    public static final Icon CheckTask = load("/icons/com/jetbrains/edu/learning/CheckTask.png"); // 16x16
    public static final Icon ResetTaskFile = load("/icons/com/jetbrains/edu/learning/ResetTaskFile.png"); // 16x16
    public static final Icon Sandbox = load("/icons/com/jetbrains/edu/learning/Sandbox.png"); // 16x16
    public static final Icon TaskDescription = load("/icons/com/jetbrains/edu/learning/TaskDescription.png"); // 13x13
    public static final Icon Download = AllIcons.Welcome.FromVCS; // 16x16
}
