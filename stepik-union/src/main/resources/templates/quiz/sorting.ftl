<#-- @ftlvariable name="ordering" type="java.util.List<com.intellij.openapi.util.Pair>" -->
<#-- @ftlvariable name="status" type="java.lang.String" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->

<#include "base_sorting.ftl"/>
<@sorting_quiz>
    <#list ordering as option>
    <div class="line">
        <div class="second">
            <#if status == "active">
                <#include "arrows.ftl"/>
            </#if>
            <div class="textarea" id="option${index}">${option.getSecond()}</div>
            <input id="index${index}" type="hidden" name="index" value="${index}">
        </div>
    </div>
        <#assign index++ />
    </#list>
</@sorting_quiz>
