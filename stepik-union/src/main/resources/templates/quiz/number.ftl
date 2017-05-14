<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.NumberQuizHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<#include "base.ftl">

<@styles>
<style>
    #text {
        display: block;
        margin: 10px auto;
        width: 100%;
    }
</style>
</@styles>

<@quiz_content>
    <#if action != "get_first_attempt" && action != "need_login" >
    <input id="text" type="text" name="value" placeholder="Input your answer here" ${disabled!""}
           value="${stepNode.getNumber()}"/>
    </#if>
</@quiz_content>
