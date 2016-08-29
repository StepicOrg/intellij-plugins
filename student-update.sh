#!/usr/bin/env bash

# This script copy the necessary jar files (student.jar and Edu-IntelliJ.jar) to classpath
#
# This files you can build running:
# *) intellij-community/python/educational-core/build.gradle
# with task: "clean :student:buildPlugin :student:jar"
# *) educational-plugins/Edu-Utils/build.gradle
# with task: "clean :Edu-IntelliJ:buildPlugin :Edu-IntelliJ:jar"
#
# or build their analog as here


from=/home/user/IdeaProjects/stepic-intellij-community/python/educational-core/gradleBuild/student/libs/*.jar
#from2=/home/user/IdeaProjects/educational-plugins/Edu-Utils/gradleBuild/Edu-IntelliJ/libs/*.jar

to=/home/user/Documents/add_to_Stepic_SDK

cp $from $to
#cp $from2 $to