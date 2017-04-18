<#-- @ftlvariable name="ordering" type="java.util.List<com.intellij.openapi.util.Pair>" -->
<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->

<#include "base_sorting.ftl"/>
<@sorting_quiz>
    <#if action != "get_first_attempt" && action != "need_login" >
        <#list ordering as option>
        <div class="line">
            <div class="second">
                <#if action == "submit">
                    <#include "arrows.ftl"/>
                </#if>
                <div class="textarea" id="option${index}">${option.getSecond()}</div>
                <input id="index${index}" type="hidden" name="index" value="${index}">
            </div>
        </div>
            <#assign index++ />
        </#list>
    </#if>
</@sorting_quiz>
