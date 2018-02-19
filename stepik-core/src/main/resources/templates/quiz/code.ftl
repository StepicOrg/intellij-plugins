<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.CodeQuizHelper" -->
<#-- @ftlvariable name="sample" type="org.stepik.api.objects.steps.Sample" -->

<#include "base_step.ftl">

<@step_content>

    ${stepNode.getSamples()}

<p><b>Limits:</b> ${stepNode.getTimeLimit()} s; ${stepNode.getMemoryLimit()} Mib</p>

</@step_content>
