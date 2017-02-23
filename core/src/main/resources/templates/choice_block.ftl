<#-- @ftlvariable name="choiceStepNode" type="com.jetbrains.tmp.learning.courseFormat.ChoiceStepNodeHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

${text}<br>

<div>
<#assign status = choiceStepNode.getStatus()/>

    <form action="${choiceStepNode.getPath()}" method="get">

    <#if status != "active">
        <#assign disabled = "disabled" />
    </#if>

    <#if status == "wrong">
        <p style="color: #dd4444">Wrong</p>
    <#elseif status == "correct">
        <p style="color: #117700">Correct</p>
    </#if>

    <#assign index = 0 />

    <#assign type=choiceStepNode.isMultipleChoice()?string("checkbox", "radio") />

    <#list choiceStepNode.getOptions() as option>
        <input type="${type}" name="option"
               value="${index}" ${disabled!""} ${option.getSecond()?string("checked", "")}> ${option.getFirst()} </input>
        <br>
        <#assign index++ />
    </#list>
        <input type="hidden" name="status" value="${status}"/>
        <input type="hidden" name="attemptId" value="${choiceStepNode.getAttemptId()?string("#")}"/>
        <input type="hidden" name="count" value="${index}"/>
        <br>

    <#if status == "empty">
        <#assign submitCaption = "Solve" />
    <#elseif status == "active">
        <#assign submitCaption = "Submit" />
    <#elseif status != "evaluation">
        <#assign submitCaption = "Solve again" />
    <#else>
        <#assign submitCaption = "Evaluation" />
        <#assign disabledSubmit = "disabled" />
    </#if>
        <input type="submit" value="${submitCaption}" ${disabledSubmit!""}/>
    </form>
</div>