<#-- @ftlvariable name="choiceStepNode" type="com.jetbrains.tmp.learning.courseFormat.ChoiceStepNodeHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

${text}<br>
<form>
<#if choiceStepNode.isMultipleChoice()>
    <#assign inputType = "checkbox" />
<#else>
    <#assign inputType = "radio" />
</#if>

<#if !choiceStepNode.isActive()>
    <#assign disabled = "disabled" />
<#else>
    <#assign disabled = "" />
</#if>

<#assign index = 0 />

<#list choiceStepNode.getOptions() as option>
    <#if option.getSecond()>
        <#assign checked = "checked" />
    <#else>
        <#assign checked = "" />
    </#if>

    <input type="${inputType}" name="options" value="${index}" ${disabled} ${checked}> ${option.getFirst()} </input><br>
    <#assign index++ />
</#list>

    <br>

<#if choiceStepNode.isEmpty()>
    <#assign submitCaption = "Solve" />
<#elseif choiceStepNode.isActive()>
    <#assign submitCaption = "Submit" />
<#else>
    <#assign submitCaption = "Solve again" />
</#if>
    <input type="submit" value="${submitCaption}"/>
</form>