<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.VideoTheoryHelper" -->
<#include "base_step.ftl">

<@styles>

<link href="https://vjs.zencdn.net/5.19/video-js.min.css" rel="stylesheet">

<style>

</style>
</@styles>

<@step_content>

    <#if stepNode.hasContent()>
    <video id="video" class="video-js vjs-default-skin" src="${stepNode.getUrl()}" preload="auto" autoplay></video>
    <#else>
    <p>Not content</p>
    </#if>

</@step_content>

<@scripts>

<script src="https://vjs.zencdn.net/5.19/video.min.js"></script>

<script>
    console.warn(document.body.innerHTML);


    var player = videojs('video', {
        controls: true
    });

    player.width(document.body.clientWidth - 20);

    window.addEventListener('resize', function(event){
        player.width(document.body.clientWidth - 20);
    });

</script>
</@scripts>