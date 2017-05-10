<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="needLogin" type="boolean" -->
<#-- @ftlvariable name="status" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.QuizHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->
<style>
    .status {
        text-transform: capitalize;
    }

    /*noinspection CssUnusedSymbol*/
    .wrong {
        color: #dd4444;
    }

    /*noinspection CssUnusedSymbol*/
    .correct {
        color: #117700;
    }
</style>

<#include "base_step.ftl">

<@step_content>

    <#macro quiz_content>
    <div>
        <#assign isHasSubmissionsRestrictions = !needLogin && stepNode.isHasSubmissionsRestrictions() />
        <#if isHasSubmissionsRestrictions>
            <#assign maxSubmissionsCount = stepNode.getMaxSubmissionsCount() />
            <#assign submissionsCount = stepNode.getSubmissionsCount() />
        <#else>
            <#assign maxSubmissionsCount = 0 />
            <#assign submissionsCount = 0 />
        </#if>

        <#assign locked = isHasSubmissionsRestrictions && (submissionsCount >= maxSubmissionsCount) />

        <form id="answer_form" action="${stepNode.getPath()}" method="get">
            <#if action != "submit" || locked>
                <#assign disabled = "disabled" />
            </#if>

            <#if status != "unchecked">
                <p class="status ${status}">${status}</p>
                <div>
                ${stepNode.getHint()}
                </div>
            </#if>

            <#nested/>

            <input id="action" type="hidden" name="action" value="${action}"/>
            <input type="hidden" name="attemptId" value="${stepNode.getAttemptId()?string("#")}"/>
            <input type="hidden" name="locked" value="${locked?string("true", "false")}"/>
            <input type="hidden" name="type" value="${stepNode.getType()}"/>
            <br>

            <#if !locked>
                <input id="submit_button" type="submit" value="Evaluation" onclick="showLoadAnimation()"/>
                <#if status != "unchecked" && action == "submit">
                    <input type="submit" value="Reset" onclick="solve_again()"/>
                </#if>
            </#if>
        </form>

        <script>
            var captions = {
                "get_first_attempt": "Click to solve",
                "get_attempt": "Click to solve again",
                "submit": "Submit",
                "need_login": "Login"
            };

            var action_element = document.getElementById("action");

            function updateSubmitCaption() {
                var action = action_element.getAttribute("value");

                var disabled = !captions.hasOwnProperty(action);
                var submitCaption = !disabled ? captions[action] : action;

                var submit = document.getElementById("submit_button");
                submit.setAttribute("value", submitCaption);
                if (disabled) {
                    submit.setAttribute("disabled", "true");
                } else {
                    submit.removeAttribute("disabled")
                }
            }
            updateSubmitCaption();

            function solve_again() {
                action_element.setAttribute("value", "get_attempt");
            }
        </script>

        <#if isHasSubmissionsRestrictions>
            <p>${maxSubmissionsCount - submissionsCount} attempts left</p>
        </#if>
    </div>
    </#macro>
</@step_content>