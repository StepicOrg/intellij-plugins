<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.StringStepNodeHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

${text}<br>

<style>
    #text {
        display: block;
        margin: 10px auto;
        width: 100%;
    }
</style>

<div>
<#assign status = stepNode.getStatus()/>

    <form action="${stepNode.getPath()}" method="get">

    <#if status != "active" || stepNode.isTextDisabled()>
        <#assign disabled = "disabled" />
    </#if>

    <#if status == "wrong">
        <p style="color: #dd4444">Wrong</p>
    <#elseif status == "correct">
        <p style="color: #117700">Correct</p>
    </#if>

        <input id="text" type="text" name="value" placeholder="Input your answer here" ${disabled!""}
               value="${stepNode.getText()}"/>
        <input type="hidden" name="status" value="${status}"/>
        <input type="hidden" name="attemptId" value="${stepNode.getAttemptId()?string("#")}"/>
        <input type="hidden" name="type" value="string"/>
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

<#assign isHasSubmissionsRestrictions = stepNode.getStepNode().getData().isHasSubmissionsRestrictions() />
<#assign maxSubmissionsCount = stepNode.getStepNode().getData().getMaxSubmissionsCount() />
<#assign submissionsCount = stepNode.getSubmissionsCount() />

<#if isHasSubmissionsRestrictions>
    <p>${submissionsCount} of ${maxSubmissionsCount} attempts</p>
</#if>

</div>