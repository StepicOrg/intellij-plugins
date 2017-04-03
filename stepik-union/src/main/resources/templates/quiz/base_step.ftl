<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.StepHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->
<#macro step_content>
<a href='${stepNode.getLink()}'>${stepNode.getLinkTitle()}</a><br>

${stepNode.getContent()}<br>

<#nested/>

</#macro>