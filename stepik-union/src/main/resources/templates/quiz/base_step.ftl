<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.StepHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->
<style>
    .adaptive-buttons {
        margin-top: 10px;
    }

    .adaptive-button {
        display: inline-block;
        margin-right: 15px;
    }

    #load_animation {
        display: none;
        position: absolute;
        width: 100%;
        height: 100%;
        left: 0;
        top: 0;
        background-color: rgba(0, 0, 0, 0.3);
    }

    #load_animation svg {
        margin: auto;
    }
</style>

<#macro step_content>

<a href='${stepNode.getLink()}'>${stepNode.getLinkTitle()}</a><br>

    <#assign status = stepNode.getStatus()/>
    <#assign action = stepNode.getAction()/>
    <#assign needLogin = action == "need_login">

    <#if stepNode.isAdaptive()>
        <#if !needLogin>
        <div class="adaptive-buttons">
            <#assign lessonId = stepNode.getParent()?string("#")/>
            <a class="adaptive-button" href="adaptive:too_easy/${lessonId}">Lesson is too easy</a>
            <a class="adaptive-button" href="adaptive:new_recommendation/${lessonId}">New recommendation</a>
            <a class="adaptive-button" href="adaptive:too_hard/${lessonId}">Lesson is too hard</a>
        </div>
        <#else>
        <form id="answer_form" action="${stepNode.getPath()}" method="get">
            <input id="action" type="hidden" name="action" value="${action}"/>
            <input id="submit_button" type="submit" value="Login" onclick="showLoadAnimation()"/>
        </form>
        </#if>
    </#if>

${stepNode.getContent()}<br>

    <#nested/>

<div id="load_animation">
    <svg xmlns="http://www.w3.org/2000/svg"  version="1.0" width="64px" height="64px" viewBox="0 0 128 128" xml:space="preserve">
            <g>
                <circle cx="16" cy="64" r="16" fill="#000000" fill-opacity="1"></circle>
                <circle cx="16" cy="64" r="14.344" fill="#000000" fill-opacity="1" transform="rotate(45 64 64)"></circle>
                <circle cx="16" cy="64" r="12.531" fill="#000000" fill-opacity="1" transform="rotate(90 64 64)"></circle>
                <circle cx="16" cy="64" r="10.75" fill="#000000" fill-opacity="1" transform="rotate(135 64 64)"></circle>
                <circle cx="16" cy="64" r="10.063" fill="#000000" fill-opacity="1" transform="rotate(180 64 64)"></circle>
                <circle cx="16" cy="64" r="8.063" fill="#000000" fill-opacity="1" transform="rotate(225 64 64)"></circle>
                <circle cx="16" cy="64" r="6.438" fill="#000000" fill-opacity="1" transform="rotate(270 64 64)"></circle>
                <circle cx="16" cy="64" r="5.375" fill="#000000" fill-opacity="1" transform="rotate(315 64 64)"></circle>
                <animateTransform attributeName="transform" type="rotate"
                                  values="0 64 64;315 64 64;270 64 64;225 64 64;180 64 64;135 64 64;90 64 64;45 64 64"
                                  calcMode="discrete" dur="720ms" repeatCount="indefinite">
                </animateTransform>
            </g>
        </svg>
</div>

<script>
    var load_animation = document.getElementById("load_animation");

    function showLoadAnimation() {
        load_animation.style.display = "flex";
    }

    //noinspection JSUnusedLocalSymbols
    function hideLoadAnimation() {
        load_animation.style.display = "none";
    }
</script>

</#macro>