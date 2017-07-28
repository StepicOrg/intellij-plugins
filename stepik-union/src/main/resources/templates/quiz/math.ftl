<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.QuizHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<#include "base_step.ftl">

<@styles>
<style>
    #text {
        display: block;
        margin: 10px auto;
        width: 100%;
    }
</style>
</@styles>

<@step_content>
    <#if action != "get_first_attempt" && action != "need_login" >
    <input id="text" name="value" placeholder="Input your answer here" ${disabled!""}
           value="${stepNode.getFormula()}"/>
    </#if>
</@step_content>
