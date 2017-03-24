<#-- @ftlvariable name="status" type="java.lang.String" -->
<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.NumberStepNodeHelper" -->
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
    <#if status != "">
    <input id="text" type="text" name="value" placeholder="Input your answer here" ${disabled!""}
           value="${stepNode.getNumber()}"/>
    </#if>

</@quiz_content>