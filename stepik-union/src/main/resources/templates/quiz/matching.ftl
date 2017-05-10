<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="option" type="org.stepik.api.objects.attempts.StringPair" -->
<#-- @ftlvariable name="ordering" type="java.util.List<org.stepik.api.objects.attempts.StringPair>" -->

<#include "base_sorting.ftl"/>
<@sorting_quiz>
    <#if action != "get_first_attempt" && action != "need_login">
        <#list ordering as option>
        <div class="line">
            <div class="first">
                <div>${option.getFirst()}</div>
            </div>
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
