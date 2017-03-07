<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.stepHelpers.ChoiceStepNodeHelper" -->

<#include "base.ftl">

<@quiz_content>
    <#assign index = 0 />

    <#assign type=stepNode.isMultipleChoice()?string("checkbox", "radio") />

    <#list stepNode.getOptions() as option>
    <label><input type="${type}" name="option"
                  value="${index}" ${disabled!""} ${option.getSecond()?string("checked", "")}/> ${option.getFirst()}
    </label>
    <br>
        <#assign index++ />
    </#list>
<input type="hidden" name="type" value="choice"/>
<input type="hidden" name="count" value="${index}"/>
</@quiz_content>

