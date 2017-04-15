<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.QuizHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->
<#include "base_step.ftl">

<@step_content>

    <#macro quiz_content>
    <div>
        <#assign status = stepNode.getStatus()/>
        <#assign isHasSubmissionsRestrictions = stepNode.isHasSubmissionsRestrictions() />
        <#assign maxSubmissionsCount = stepNode.getMaxSubmissionsCount() />
        <#assign submissionsCount = stepNode.getSubmissionsCount() />
        <#assign locked = isHasSubmissionsRestrictions && (submissionsCount >= maxSubmissionsCount) />

        <form id="answer_form" action="${stepNode.getPath()}" method="get">
            <#if status != "active" && status != "active_wrong">
                <#assign disabled = "disabled" />
            </#if>

            <#if status == "wrong" || status == "active_wrong">
                <p style="color: #dd4444">Wrong</p>
                <div>
                ${stepNode.getHint()}
                </div>
            <#elseif status == "correct">
                <p style="color: #117700">Correct</p>
            </#if>

            <#nested/>

            <input id="status" type="hidden" name="status" value="${status}"/>
            <input type="hidden" name="attemptId" value="${stepNode.getAttemptId()?string("#")}"/>
            <input type="hidden" name="locked" value="${locked?string("true", "false")}"/>
            <input type="hidden" name="type" value="${stepNode.getType()}"/>
            <br>

            <#if !locked>
                <input id="submit_button" type="submit" value="Evaluation"/>
                <#if status == "active_wrong">
                    <input type="submit" value="Reset" onclick="solve_again()"/>
                </#if>
            </#if>
        </form>

        <script>
            var status_element = document.getElementById("status");

            function updateSubmitCaption() {
                var status = status_element.getAttribute("value");

                var submitCaption;
                var disabled = false;

                if (status === "") {
                    submitCaption = "Click to solve";
                } else if (status === "active" || status === "active_wrong") {
                    submitCaption = "Submit";
                } else if (status === "need_login") {
                    submitCaption = "Login";
                } else if (status !== "evaluation") {
                    submitCaption = "Click to solve again";
                } else {
                    submitCaption = "Evaluation";
                    disabled = true;
                }

                var submit = document.getElementById("submit_button");
                submit.setAttribute("value", submitCaption);
                if (disabled) {
                    submit.setAttribute("disabled", true);
                } else {
                    submit.removeAttribute("disabled")
                }
            }
            updateSubmitCaption();
                <#if status == "active_wrong">
                function solve_again() {
                    status_element.setAttribute("value", "");
                }
                </#if>
        </script>

        <#if isHasSubmissionsRestrictions && status != "need_login">
            <p>${maxSubmissionsCount - submissionsCount} attempts left</p>
        </#if>
    </div>
    </#macro>
</@step_content>