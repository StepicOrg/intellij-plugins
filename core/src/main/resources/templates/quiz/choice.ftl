<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.ChoiceStepNodeHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<#include "base.ftl">

<@quiz_content>
    <#assign index = 0 />

    <#assign type=stepNode.isMultipleChoice()?string("checkbox", "radio") />

    <#list stepNode.getOptions() as option>
    <input type="${type}" name="option"
           value="${index}" ${disabled!""} ${option.getSecond()?string("checked", "")}> ${option.getFirst()} </input>
    <br>
        <#assign index++ />
    </#list>
<input type="hidden" name="type" value="choice"/>
<input type="hidden" name="count" value="${index}"/>
</@quiz_content>

