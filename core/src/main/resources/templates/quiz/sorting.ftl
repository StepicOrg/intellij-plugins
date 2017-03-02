<#include "base_sorting.ftl"/>
<@sorting_quiz>
    <#list ordering as option>
    <div class="line">
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
