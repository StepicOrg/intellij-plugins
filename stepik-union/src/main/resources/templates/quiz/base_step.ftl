<#-- @ftlvariable name="loader" type="java.lang.String" -->
<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.StepHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<#include "../template.ftl">

<@styles>
<style>
    .adaptive-buttons {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        margin-top: 10px;
    }

    .adaptive-button {
        display: flex;
        flex-grow: 1;
        padding: 10px;
        border: 1px solid darkgray;
        border-radius: 5px;
        text-decoration: none;
        min-width: 100px;
        margin-right: 5px;
    }
</style>
    <#macro styles>
        <#nested/>
    </#macro>
</@styles>

<@content>
    <#macro step_content>
        <#assign status = stepNode.getStatus()/>
        <#assign action = stepNode.getAction()/>
        <#assign needLogin = action == "need_login">

        <#if stepNode.isAdaptive()>
            <#if !needLogin>
            <div class="adaptive-buttons">
                <#assign lessonId = stepNode.getParent()?string("#")/>
                <a class="adaptive-button" href="adaptive:too_easy/${lessonId}">Lesson is too easy</a>
                <#if stepNode.solvedLesson() >
                    <a class="adaptive-button" href="adaptive:solved/${lessonId}">New recommendation</a>
                <#else>
                    <div class="adaptive-button">New recommendation</div>
                </#if>
                <a class="adaptive-button" href="adaptive:too_hard/${lessonId}">Lesson is too hard</a>
            </div>
            <#else>
            <button type="button" onclick="showLogin()">Login</button>
            </#if>
        </#if>

    ${stepNode.getContent()}<br>
        <#nested/>
    </#macro>
</@content>

<@scripts>
    <#macro scripts>
        <#nested/>
    </#macro>
</@scripts>
