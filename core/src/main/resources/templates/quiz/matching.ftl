<#-- @ftlvariable name="status" type="java.lang.String" -->
<#-- @ftlvariable name="backgroundColor" type="java.lang.String" -->
<#-- @ftlvariable name="darcula" type="java.lang.Boolean" -->
<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.stepHelpers.MatchingStepNodeHelper" -->

<#if darcula>
    <#assign backgroundColor = "#3C3F41"/>
<#else>
    <#assign backgroundColor = "white"/>
</#if>

<style>
    input[type="text"] {
        width: 100%;
        padding: 15px;
        box-sizing: border-box;
        color: black;
        background-color: ${backgroundColor};
        border: 1px solid lightgray;
        border-radius: 5px;
        margin: 0;
    }

    .line {
        display: flex;
        flex-direction: row;
        margin: 5px 0;
    }

    .second {
        display: flex;
        flex-direction: row;
        width: 100%;
    }

    .first {
        display: flex;
        padding: 15px;
        border: 1px solid lightgray;
        border-radius: 5px;
        width: 50%;
        margin-right: 5px;
    }

    .buttons {
        display: flex;
        width: 24px;
        flex-direction: column;
        margin-right: 5px;
    }

    .first div {
        margin: auto 0;
    }

    .button {
        display: flex;
        width: 0;
        height: 0;
        border: 12px solid ${backgroundColor};
    }

    .button-up {
        border-color: ${backgroundColor};
        border-bottom-color: black;
        margin-bottom: 5px;
    }

    .button-down {
        border-color: ${backgroundColor};
        border-top-color: black;
        margin-top: 5px;
    }

    .button-up:hover {
        border-bottom-color: blue;
    }

    .button-down:hover {
        border-top-color: blue;
    }

    .button-up:active {
        border-bottom-color: red;
    }

    .button-down:active {
        border-top-color: red;
    }

</style>

<#include "base.ftl">

<@quiz_content>
    <#assign index = 0 />
    <#assign ordering = stepNode.getOrdering() />
    <#assign count = ordering?size />

    <#list ordering as option>
    <div class="line">
        <div class="first">
            <div>${option.getFirst()}</div>
        </div>
        <div class="second">
            <#if status == "active">
                <div class="buttons">
                    <div index="${index}" class="button <#if index != 0>button-up</#if>"></div>
                    <div index="${index}" class="button <#if index != (count-1)>button-down</#if>"></div>
                </div>
            </#if>
            <input id="option${index}" type="text" name="option" value="${option.getSecond()}" readonly/>
            <input id="index${index}" type="hidden" name="index" value="${index}">
        </div>
    </div>
        <#assign index++ />
    </#list>
<input type="hidden" name="type" value="matching"/>
<input type="hidden" name="count" value="${count}"/>
</@quiz_content>

<script>
    var buttons = document.getElementsByClassName("button-up");
    for (var i = 0; i < buttons.length; i++) {
        buttons[i].addEventListener("click", function (event) {
            swapOptions(event, -1);
        });
    }

    buttons = document.getElementsByClassName("button-down");
    for (i = 0; i < buttons.length; i++) {
        buttons[i].addEventListener("click", function (event) {
            swapOptions(event, 1);
        });
    }

    function swapOptions(event, direction) {
        var button = event.target;
        var index = button.getAttribute("index");
        var option1 = document.querySelector("#option" + (+index + direction));
        var option2 = document.querySelector("#option" + index);
        swapValue(option1, option2);

        var index1 = document.querySelector("#index" + (+index + direction));
        var index2 = document.querySelector("#index" + index);
        swapValue(index1, index2);
    }

    function swapValue(option1, option2) {
        if (option1 && option2) {
            var attribute = "value";
            var value = option1.getAttribute(attribute);
            option1.setAttribute(attribute, option2.getAttribute(attribute));
            option2.setAttribute(attribute, value);
        }
    }
</script>