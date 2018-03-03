<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.CodeQuizHelper" -->
<#-- @ftlvariable name="sample" type="org.stepik.api.objects.steps.Sample" -->

<#include "base_step.ftl">

<@step_content>

    ${stepNode.getSamples()}

<p><b>Time limit:</b> ${stepNode.getTimeLimit()} seconds <br>
    <b>Memory limit:</b> ${stepNode.getMemoryLimit()} MB
</p>

</@step_content>
