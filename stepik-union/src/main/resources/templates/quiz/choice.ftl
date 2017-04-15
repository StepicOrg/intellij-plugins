<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.ChoiceQuizHelper" -->

<#include "base.ftl">

<@quiz_content>
    <#if action != "get_first_attempt" && action != "need_login" >
        <#assign index = 0 />

        <#assign type=stepNode.isMultipleChoice()?string("checkbox", "radio") />

        <#list stepNode.getOptions() as option>
        <label><input type="${type}" name="option"
                      value="${index}" ${disabled!""} ${option.getSecond()?string("checked", "")}/> ${option.getFirst()}
        </label>
        <br>
            <#assign index++ />
        </#list>

    <input type="hidden" name="count" value="${index}"/>
    </#if>
</@quiz_content>

