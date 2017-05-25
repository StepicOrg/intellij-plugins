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

<select id="resolution-picker" title="Resolution">
    <#assign quality = stepNode.getQuality()/>
    <#list stepNode.getVideoUrls() as url>
        <#assign selected = quality == url.getQuality()/>
        <option value="${url.getQuality()?string('#')}" ${selected?string('selected', '')}>${url.getQuality()?string('#')}</option>
    </#list>
</select>
</@step_content>

<@scripts>

<script src="https://vjs.zencdn.net/5.19/video.min.js"></script>

<script>
    var player = videojs('video', {
        controls: true
    });

    player.width(document.body.clientWidth - 20);

    window.addEventListener('resize', function () {
        player.width(document.body.clientWidth - 20);
    });

    var urls_map = {};
        <#list stepNode.getVideoUrls() as url>
        urls_map[${url.getQuality()?string('#')}] = "${url.getUrl()}";
        </#list>

    var resolution_picker = document.getElementById('resolution-picker');

    resolution_picker.addEventListener('change', function () {
        var value = resolution_picker.value;

        if (!value) {
            return;
        }

        java.setVideoQuality(parseInt(value));

        player.pause();
        var whereYouAt = player.currentTime();
        player.src({src: urls_map[value], type: 'video/mp4'});
        player.on('canplay', function () {
            player.currentTime(whereYouAt);
            player.play();
        })
    })
</script>
</@scripts>