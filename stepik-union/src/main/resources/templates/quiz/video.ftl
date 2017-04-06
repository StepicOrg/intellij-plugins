<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.VideoStepNodeHelper" -->
<#include "base_step.ftl">

<@step_content>

    <video src="${stepNode.getUrl()}" width="100%" preload controls autoplay></video>

</@step_content>