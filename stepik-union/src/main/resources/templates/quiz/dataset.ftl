<#-- @ftlvariable name="status" type="java.lang.String" -->
<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.stepHelpers.DatasetStepNodeHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

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

<#include "base.ftl">

<@quiz_content>
<div id="time-left">5 minutes</div>
    <#assign dataset_url = stepNode.getDatasetUrl()/>
    <#assign reply_url = stepNode.getReplyUrl()/>

    <#if dataset_url != "">
    <a class="dataset-url" href="inner:${stepNode.getBaseUrl()}${dataset_url}" data-step-path="${stepNode.getPath()}"
       data-content-type="application/txt" data-file-prefix="dataset" data-file-ext=".txt">Download dataset</a>
    </#if>

    <#if reply_url != "" >
    <a class="dataset-url" href="inner:${stepNode.getBaseUrl()}${reply_url}" data-step-path="${stepNode.getPath()}"
       data-content-type="application/txt" data-file-prefix="reply" data-file-ext=".txt">Download last submission
        dataset</a>
    </#if>
<textarea id="text" class="row" name="value"
          placeholder="Input your answer here" ${disabled!""}>${stepNode.getData()}</textarea>
    <#if status == "active">
    <input id="filename" type="submit" name="filename" value="Send file">
    </#if>
<input id="isFromFile" type="hidden" name="isFromFile" value="false">
</@quiz_content>

<script>
    var time_left = ${stepNode.getTimeLeft()};
    var clock = document.getElementById("time-left");
    <#if status == "active">
    var active = true;
    <#else>
    var active = false;
    </#if>

    if (active) {
        if (time_left > 0) {
            clock.innerHTML = timeToString(time_left);
        }
        var timerId = setInterval(function () {
            time_left--;

            if (time_left <= 0) {
                clearTimeout(timerId);
                clock.innerHTML = "Time left (5 minutes)";
                document.getElementById("text").setAttribute("readonly", true);
                document.getElementById("filename").setAttribute("type", "hidden");
                document.getElementById("status").setAttribute("value", "timeLeft");
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