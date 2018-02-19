<#-- @ftlvariable name="loader" type="java.lang.String" -->
<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.StepHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<#include "../template.ftl">

<@styles>
<style>
    .adaptive-buttons {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        margin-top: 10px;
    }

    .adaptive-button {
        display: flex;
        flex-grow: 1;
        padding: 10px;
        border: 1px solid darkgray;
        border-radius: 5px;
        text-decoration: none;
        min-width: 100px;
        margin-right: 5px;
    }

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
    <#macro styles>
        <#nested/>
    </#macro>
</@styles>

<@content>
    <#macro step_content>
        <#assign status = stepNode.getStatus()/>
        <#assign action = stepNode.getAction()/>
        <#assign needLogin = stepNode.needLogin()>

        <#if stepNode.isAdaptive()>
            <#if !needLogin>
            <div class="adaptive-buttons">
                <#assign lessonId = stepNode.getParent()?string("#")/>
                <a class="adaptive-button" href="adaptive:too_easy/${lessonId}">Lesson is too easy</a>
                <#if stepNode.solvedLesson() >
                    <a class="adaptive-button" href="adaptive:solved/${lessonId}">New recommendation</a>
                <#else>
                    <div class="adaptive-button">New recommendation</div>
                </#if>
                <a class="adaptive-button" href="adaptive:too_hard/${lessonId}">Lesson is too hard</a>
            </div>
            <#else>
            <button type="button" onclick="showLogin()">Login</button>
            </#if>
        </#if>

        ${stepNode.getContent()}<br>
    <div>
        <form id="answer_form" action="${stepNode.getPath()}">
            <#if action != "submit" || !stepNode.hasSubmitButton()>
                <#assign disabled = "disabled" />
            </#if>

            <#if stepNode.canSubmit() && status != "unchecked">
            <#--noinspection FtlReferencesInspection-->
                <p class="status ${status}">${status} ${stepNode.isModified()?string('*', '')}</p>
                <div>
                <#--noinspection FtlReferencesInspection-->
                    ${stepNode.getHint()}
                </div>
            </#if>

            <#nested/>

            <input id="action" type="hidden" name="action" value="${action}"/>
            <input type="hidden" name="locked" value="${(!stepNode.hasSubmitButton())?string("true", "false")}"/>
            <input type="hidden" name="type" value="${stepNode.getType()}"/>
            <br>

            <#if stepNode.canSubmit()>
            <#--noinspection FtlReferencesInspection-->
                <input type="hidden" name="attemptId" value="${stepNode.getAttemptId()?string("#")}"/>
            </#if>

            <#if stepNode.hasSubmitButton()>
            <#--noinspection FtlReferencesInspection-->
                <input type="hidden" name="attemptId" value="${stepNode.getAttemptId()?string("#")}"/>
                <input id="submit_button" type="submit" value="Evaluation" onclick="showLoadAnimation()"/>
                <#if status != "unchecked" && action == "submit">
                    <input type="submit" value="Reset" onclick="solve_again()"/>
                </#if>
            </#if>

            <#if needLogin>
                <button type="button" onclick="showLogin()">Login</button>
            </#if>

            <#if stepNode.hasNextStep()>
                <input type="submit" value="Next step" onclick="next_step()"/>
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

            function next_step() {
                action_element.setAttribute("value", "next_step");
            }
        </script>

    <#--noinspection FtlReferencesInspection-->
        <#if stepNode.canSubmit() && stepNode.isHasSubmissionsRestrictions()>
        <#--noinspection FtlReferencesInspection-->
            <p>${stepNode.submissionsLeft()} attempts left</p>
        </#if>
    </div>
    </#macro>
</@content>

<@scripts>
    <#macro scripts>
        <#nested/>
    </#macro>
</@scripts>
