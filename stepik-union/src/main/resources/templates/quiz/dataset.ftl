<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.DatasetQuizHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<#include "base_step.ftl">

<@styles>
<style>
    .row {
        display: block;
        margin: 10px auto;
        width: 100%;
    }

    #text {
        min-height: 200px;
    }

    .dataset-url {
        display: inline-block;
        padding: 5px;
        text-decoration: none;
        border: 1px solid beige;
        background-color: beige;
        border-radius: 5px;
    }

    .dataset-url:hover {
        background-color: darkkhaki;
    }
</style>
</@styles>

<@step_content>
    <#if action != "get_first_attempt" && action != "need_login">
    <div id="time-left">5 minutes</div>
        <#assign dataset_url = stepNode.getDatasetUrl()/>
        <#assign reply_url = stepNode.getReplyUrl()/>

        <#if dataset_url != "">
        <a class="dataset-url" href="inner:${stepNode.getBaseUrl()}${dataset_url}"
           data-step-path="${stepNode.getPath()}"
           data-content-type="application/txt" data-file-prefix="dataset" data-file-ext=".txt">Download dataset</a>
        </#if>

        <#if reply_url != "" >
        <a class="dataset-url" href="inner:${stepNode.getBaseUrl()}${reply_url}" data-step-path="${stepNode.getPath()}"
           data-content-type="application/txt" data-file-prefix="reply" data-file-ext=".txt">Download last submission
            dataset</a>
        </#if>
    <textarea id="text" class="row" name="value"
              placeholder="Input your answer here" ${disabled!""}>${stepNode.getData()}</textarea>
        <#if action == "submit">
        <input id="filename" type="submit" name="filename" value="Send file">
        </#if>
    <input id="isFromFile" type="hidden" name="isFromFile" value="false">
    </#if>
</@step_content>

<@scripts>
<script>
    var time_left = ${stepNode.getTimeLeft()};
    var clock = document.getElementById("time-left");
    var active = "${action}" === "submit";

    if (active) {
        //noinspection JSUnusedAssignment
        if (time_left > 0) {
            //noinspection JSUnusedAssignment
            clock.innerHTML = timeToString(time_left);
        }
        var timerId = setInterval(function () {
            time_left--;

            if (time_left <= 0) {
                clearTimeout(timerId);
                clock.innerHTML = "Time left (5 minutes)";
                document.getElementById("text").setAttribute("readonly", "true");
                document.getElementById("filename").setAttribute("type", "hidden");
                document.getElementById("action").setAttribute("value", "get_attempt");
                updateSubmitCaption();
                return;
            }
            clock.innerHTML = timeToString(time_left);
        }, 1000);
    }

    function timeToString(time) {
        var min = Math.floor(time / 60);
        var sec = time - min * 60;

        return "Time left: " + (min > 0 ? min + " m " : "") + sec + " s";
    }

    document.getElementById("filename").onclick = function () {
        document.getElementById("isFromFile").setAttribute("value", "true");
    }
</script>
</@scripts>
