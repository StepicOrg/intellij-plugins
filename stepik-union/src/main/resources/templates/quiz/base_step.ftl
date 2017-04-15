<#-- @ftlvariable name="status" type="java.lang.String" -->
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

    <#if stepNode.isAdaptive() && status != "need_login">
    <div class="adaptive-buttons">
        <#assign lessonId = stepNode.getParent()?string("#")/>
        <a class="adaptive-button" href="adaptive:too_easy/${lessonId}">Lesson is too easy</a>
        <a class="adaptive-button" href="adaptive:new_recommendation/${lessonId}">New recommendation</a>
        <a class="adaptive-button" href="adaptive:too_hard/${lessonId}">Lesson is too hard</a>
    </div>
    </#if>

${stepNode.getContent()}<br>

    <#nested/>

</#macro>