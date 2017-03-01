<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.stepHelpers.StringStepNodeHelper" -->
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
       value="${stepNode.getText()}"/>
<input type="hidden" name="type" value="string"/>
</@quiz_content>