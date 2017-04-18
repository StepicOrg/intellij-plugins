<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.StringQuizHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<style>
    #text {
        display: block;
        margin: 10px auto;
        width: 100%;
    }
</style>

<#include "base.ftl">

<@quiz_content>
    <#if action != "get_first_attempt" && action != "need_login" >
    <input id="text" type="text" name="value" placeholder="Input your answer here" ${disabled!""}
           value="${stepNode.getText()}"/>
    </#if>
</@quiz_content>