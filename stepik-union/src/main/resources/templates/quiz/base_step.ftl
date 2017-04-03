<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.StepHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->
<style>
    .adaptive-buttons {
        margin-top: 10px;
    }
    .adaptive-button {
        display: inline-block;
        margin-right: 15px;
    }
</style>

<#macro step_content>

<a href='${stepNode.getLink()}'>${stepNode.getLinkTitle()}</a><br>

<#if stepNode.isAdaptive()>
<div class="adaptive-buttons">
    <a class="adaptive-button" href="adaptive:too_easy">Lesson is too easy</a>
    <a class="adaptive-button" href="adaptive:new_recommendation">New recommendation</a>
    <a class="adaptive-button" href="adaptive:too_hard">Lesson is too hard</a>
</div>
</#if>

${stepNode.getContent()}<br>

<#nested/>

</#macro>