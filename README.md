# intellij-plugins

## Building
I. Setup core <br>
1. clone this https://github.com/StepicOrg/intellij-community<br>
2. checkout `stepik_core` or `develop3`<br>
3. set up your Idea sandbox directory  <br>
https://github.com/StepicOrg/intellij-community/blob/Stepik_core/python/educational-core/build.gradle#L42<br>
You can see this dir in SDK configuration ( II.3 )<br>
- to build core run build.gradle with `clean :stepik-student:buildPlugin :stepik-student:jar` task <br>
 fe: `gradle -b build.gradle clean :stepik-student:buildPlugin :stepik-student:jar`<br>
or use for it shell scripts from intellij-plugins<br>
or configure Gradle run configuration<br>

II. Getting plugin<br>
1. clone intellij-plugins https://github.com/StepicOrg/intellij-plugins<br>
2. download last stable version IntelliJ Community<br>
3. set up SDK on it <br>
http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html<br>
4. add sources of IntelliJ IDEA to your plugin sdk source roots. <br>
Use `stepik_core` or `develop3` brunch. <br>
5. add the following plugins' jars to classpath of your Intellij Plugin Sdk: <br>
- stepik-student-{ver}.jar  (built from core) <br>
- idea-junit.jar (idea_home/plugins/junit/lib/idea-junit.jar)<br>

III. Set up run configuration<br>
1. Create run configuration of "Plugin" type<br>
2. set up "Before launch" options:<br>
 + add Gradle task to build core<br>
 + add student-update.sh to copy new stepik-student-{ver}.jar in classpath<br>
 + add make task<br>

Now you can run your "Plugin" configuration 

## Install

### Stable version

### EAP version

1. install "edu intellij" and "Python" plugins from JetBrains repository
2. download plugins from here
https://drive.google.com/drive/u/0/folders/0B3r_Au4BpPbwSWRFUmlhTlRLaE0
3. install them
