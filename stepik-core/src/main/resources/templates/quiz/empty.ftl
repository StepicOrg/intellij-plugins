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
        <button type="button" onclick="showLogin()">Login</button>
    </div>
    </#if>

</@content>

<@scripts>
    <#macro scripts>
        <#nested/>
    </#macro>
</@scripts>
