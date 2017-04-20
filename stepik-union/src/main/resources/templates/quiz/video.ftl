<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.VideoTheoryHelper" -->
<#include "base_step.ftl">

<@step_content>

    <#if stepNode.hasContent()>
    <video src="${stepNode.getUrl()}" width="100%" preload controls autoplay></video>
    <#else>
    <p>Not content</p>
    </#if>

</@step_content>