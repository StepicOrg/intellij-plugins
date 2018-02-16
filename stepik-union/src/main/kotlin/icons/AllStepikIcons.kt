package icons

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object AllStepikIcons {
    val stepikLogo = load("/icons/stepikLogo.png") // 16x16
    val stepikLogoBig = load("/icons/stepikLogoBig.png") // 24x24
    val stepikLogoSmall = load("/icons/stepikLogoSmall.png") // 13x13

    private fun load(path: String): Icon {
        return IconLoader.getIcon(path, AllStepikIcons::class.java)
    }

    object ProjectTree {
        val course = load("/icons/projectTree/course.png") // 16x16
        val courseCorrect = load("/icons/projectTree/courseCorrect.png") // 16x16
        val module = load("/icons/projectTree/module.png") // 16x16
        val moduleCorrect = load("/icons/projectTree/moduleCorrect.png") // 16x16
        val lesson = load("/icons/projectTree/lesson.png") // 16x16
        val lessonCorrect = load("/icons/projectTree/lessonCorrect.png") // 16x16
        val stepCode = load("/icons/projectTree/stepCode.png") // 16x16
        val stepCodeCorrect = load("/icons/projectTree/stepCodeCorrect.png") // 16x16
        val stepProblem = load("/icons/projectTree/stepProblem.png") // 16x16
        val stepProblemCorrect = load("/icons/projectTree/stepProblemCorrect.png") // 16x16
        val stepText = load("/icons/projectTree/stepText.png") // 16x16
        val stepTextCorrect = load("/icons/projectTree/stepTextCorrect.png") // 16x16
        val stepVideo = load("/icons/projectTree/stepVideo.png") // 16x16
        val stepVideoCorrect = load("/icons/projectTree/stepVideoCorrect.png") // 16x16
        val sandbox = load("/icons/projectTree/sandbox.png") // 16x16
    }

    object ToolWindow {
        val checkTask = load("/icons/toolWindow/checkTask.png") // 16x16
        val resetTaskFile = load("/icons/toolWindow/resetTaskFile.png") // 16x16
        val download = AllIcons.Welcome.FromVCS!! // 16x16
    }
}
