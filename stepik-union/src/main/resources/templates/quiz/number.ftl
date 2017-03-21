<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.stepHelpers.NumberStepNodeHelper" -->
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
<input id="text" type="text" name="value" placeholder="Input your answer here" ${disabled!""}
       value="${stepNode.getNumber()}"/>

</@quiz_content>