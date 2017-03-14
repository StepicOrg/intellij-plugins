<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.stepHelpers.DatasetStepNodeHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<style>
    #text {
        display: block;
        margin: 10px auto;
        width: 100%;
    }
</style>

<script>
    var time_left = ${stepNode.getTimeLeft()};
    var clock = document.getElementById("time-left");

    var timerId = setTimeout(function () {
        time_left--;
        if (time_left <= 0) {
            clearTimeout(timerId);
            clock.innerHTML = "Time left";
            return;
        }
        var min = time_left / 60;
        var sec = time_left - min * 60;
        clock.innerHTML = "Time left:" + (min > 0 ? min + " m") + sec + " s";
    }, 1000);
</script>

<#include "base.ftl">

<@quiz_content>
<div id="time-left">5 minutes</div>
    <#assign dataset_url = stepNode.getDatasetUrl()/>
    <#assign reply_url = stepNode.getReplyUrl()/>

    <#if dataset_url != "">
    <a href="${stepNode.getBaseUrl()}${dataset_url}">Download dataset</a>
    </#if>

    <#if reply_url != "" >
    <a href="${stepNode.getBaseUrl()}${reply_url}">Download last submission dataset</a>
    </#if>
<textarea id="text" name="value" placeholder="Input your answer here" ${disabled!""}>${stepNode.getData()}</textarea>

</@quiz_content>