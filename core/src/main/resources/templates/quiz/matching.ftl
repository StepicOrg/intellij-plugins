<#-- @ftlvariable name="option" type="org.stepik.api.objects.attempts.Pair" -->

<#include "base_sorting.ftl"/>
<@sorting_quiz>
    <#list ordering as option>
    <div class="line">
        <div class="first">
            <div>${option.getFirst()}</div>
        </div>
        <div class="second">
            <#if status == "active">
                <#include "arrows.ftl"/>
            </#if>
            <input id="option${index}" type="text" name="option" value="${option.getSecond()}" readonly/>
            <input id="index${index}" type="hidden" name="index" value="${index}">
        </div>
    </div>
        <#assign index++ />
    </#list>
</@sorting_quiz>
