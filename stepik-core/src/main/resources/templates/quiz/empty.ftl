<#-- @ftlvariable name="authenticated" type="java.lang.Boolean" -->
<#-- @ftlvariable name="description" type="java.lang.String" -->

<#include "../template.ftl">

<@styles>
    <#macro styles>
        <#nested/>
    </#macro>
</@styles>

<@content>
    ${description}

    <#if !authenticated>
    <div>
        <form id="answer_form" action="/">
            <input id="action" type="hidden" name="action"/>
            <input type="submit" onclick="login()" value="Login"/>
        </form>
    </div>
    </#if>

</@content>

<@scripts>
    <#macro scripts>
        <#nested/>
    </#macro>
</@scripts>
