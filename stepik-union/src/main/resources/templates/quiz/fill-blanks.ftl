<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.QuizHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<#include "base_step.ftl">

<@styles>
<style>
    .field {
        background-color: white;
        border: 1px solid lightgray;
        border-radius: 0;
        outline: none;
        font-size: inherit;
        font-family: inherit;

    }

    .field_input {
        min-width: 10px;
        width: 10px;
        text-align: center;
    }

    .field:focus {
        border: 1px solid blue;
        border-radius: 0;
    }

    #input-buffer {
        position: absolute;
        top: -1000px;
        left: -1000px;
        visibility: hidden;
        white-space: nowrap;
    }
</style>
</@styles>

<@step_content>
    <#if action != "get_first_attempt" && action != "need_login" >
    <div id="input-buffer"></div>
        <#assign values = stepNode.getBlanks()/>
        <#assign index = 0 />

        <#list stepNode.getComponents() as component>
            <#if component.getType() == "text">
            ${component.getText()}
            <#else>
                <#if (values?size > index) >
                    <#assign value = values[index]>
                <#else>
                    <#assign value = component.getText()>
                </#if>
                <#assign index++ />
                <#if component.getType() == "input">
                <input class="field field_input" value="${value}" title="Write" ${disabled!""} oninput="resize(this);"
                       onload="resize(this);"/>
                <#elseif component.getType() == "select">
                <select class="field" title="Select" ${disabled!""}>
                    <option value="<select>">&lt;?&gt;</option>
                    <#list component.getOptions() as option>
                        <#assign selected = (option == value)?string("selected", "")/>
                        <option value="${option}" ${selected}>${option}</option>
                    </#list>
                </select>
                </#if>
            </#if>
        </#list>
    </#if>
</@step_content>

<@scripts>
<script>
    var buffer = document.getElementById('input-buffer');
    function resize(input) {
        buffer.innerText = input.value;
        input.style.width = (buffer.offsetWidth + 5) + "px";
    }

    var inputs = document.getElementsByClassName("field_input");
    for (var i = 0; i < inputs.length; i++) {
        var element = inputs.item(i);
        resize(element);
    }
</script>
</@scripts>
