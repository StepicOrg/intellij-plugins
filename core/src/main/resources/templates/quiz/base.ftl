<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.stepHelpers.ChoiceStepNodeHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->
<#macro quiz_content>
${text}<br>

<div>
    <#assign status = stepNode.getStatus()/>
    <#assign isHasSubmissionsRestrictions = stepNode.getStepNode().getData().isHasSubmissionsRestrictions() />
    <#assign maxSubmissionsCount = stepNode.getStepNode().getData().getMaxSubmissionsCount() />
    <#assign submissionsCount = stepNode.getSubmissionsCount() />
    <#assign locked = isHasSubmissionsRestrictions && (submissionsCount >= maxSubmissionsCount) />

    <form action="${stepNode.getPath()}" method="get">
        <#if status != "active">
            <#assign disabled = "disabled" />
        </#if>

        <#if status == "wrong">
            <p style="color: #dd4444">Wrong</p>
        <#elseif status == "correct">
            <p style="color: #117700">Correct</p>
        </#if>

        <#nested/>

        <input type="hidden" name="status" value="${status}"/>
        <input type="hidden" name="attemptId" value="${stepNode.getAttemptId()?string("#")}"/>
        <input type="hidden" name="locked" value="${locked?string("true", "false")}"/>
        <br>

        <#if !locked>
            <#if status == "">
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
        </#if>
    </form>

    <#if isHasSubmissionsRestrictions>
        <p>${maxSubmissionsCount - submissionsCount} attempts left</p>
    </#if>

</div>
</#macro>