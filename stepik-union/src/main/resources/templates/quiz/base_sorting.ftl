<#-- @ftlvariable name="backgroundColor" type="java.lang.String" -->
<#-- @ftlvariable name="darcula" type="java.lang.Boolean" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.MatchingQuizHelper" -->

<#macro sorting_quiz>
    <#if darcula>
        <#assign backgroundColor = "#3C3F41"/>
    <#else>
        <#assign backgroundColor = "white"/>
    </#if>

    <#include "base.ftl">

    <@styles>
    <style>
        .textarea {
            width: 100%;
            padding: 15px;
            box-sizing: border-box;
            color: black;
            background-color: ${backgroundColor};
            border: 1px solid lightgray;
            border-radius: 5px;
            margin: 0;
            display: flex;
            overflow-wrap: break-word;
        }

        .line {
            display: flex;
            flex-direction: row;
            margin: 5px 0;
            width: 100%;
            min-width: 200px;
        }

        .right {
            display: flex;
            flex-direction: row;
            width: 100%;
        }

        .left {
            display: flex;
            padding: 15px;
            border: 1px solid lightgray;
            border-radius: 5px;
            width: 50%;
            margin-right: 5px;
            overflow-wrap: break-word;
        }

        .buttons {
            display: flex;
            width: 24px;
            flex-direction: column;
            margin-right: 5px;
            justify-content: space-around;
        }

        .left div {
            margin: auto 0;
        }

        .button {
            display: flex;
            margin: auto;
        }

        .button:hover {
            stroke: black;
        }

        .button:active {
            stroke-width: 6;
        }

    </style>
    </@styles>

    <@quiz_content>
        <#assign index = 0 />
        <#assign ordering = stepNode.getOrdering() />
        <#assign count = ordering?size />


        <#nested/>

    <input type="hidden" name="count" value="${count}"/>
    </@quiz_content>

    <@scripts>
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
            swapInnerHtml(option1, option2);

            var index1 = document.querySelector("#index" + (+index + direction));
            var index2 = document.querySelector("#index" + index);
            swapValue(index1, index2);
        }

        function swapInnerHtml(option1, option2) {
            if (option1 && option2) {
                var value = option1.innerHTML;
                option1.innerHTML = option2.innerHTML;
                option2.innerHTML = value;
            }
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
    </@scripts>
</#macro>
