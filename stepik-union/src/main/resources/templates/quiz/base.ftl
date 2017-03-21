<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.StepHelper" -->
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

        <#if status == "wrong">
            <div>
            ${stepNode.getHint()}
            </div>
        </#if>

        <#nested/>

        <input id="status" type="hidden" name="status" value="${status}"/>
        <input type="hidden" name="attemptId" value="${stepNode.getAttemptId()?string("#")}"/>
        <input type="hidden" name="locked" value="${locked?string("true", "false")}"/>
        <input type="hidden" name="type" value="${stepNode.getType()}"/>
        <br>

        <#if !locked>
            <input id="submit" type="submit" value="Evaluation"/>
        </#if>
    </form>

    <script>
        function updateSubmitCaption() {
            var status = document.getElementById("status").getAttribute("value");

            var submitCaption;
            var disabled = false;

            if (status == "") {
                submitCaption = "Solve";
            } else if (status == "active") {
                submitCaption = "Submit";
            } else if (status != "evaluation") {
                submitCaption = "Solve again";
            } else {
                submitCaption = "Evaluation";
                disabled = true;
            }

            document.getElementById("submit").setAttribute("value", submitCaption);
            var submit = document.getElementById("submit");
            if (disabled) {
                submit.setAttribute("disabled", true);
            } else {
                submit.removeAttribute("disabled")
            }
        }
        updateSubmitCaption();
    </script>

    <#if isHasSubmissionsRestrictions>
        <p>${maxSubmissionsCount - submissionsCount} attempts left</p>
    </#if>

</div>
</#macro>