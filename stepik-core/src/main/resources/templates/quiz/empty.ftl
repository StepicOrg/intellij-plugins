<#-- @ftlvariable name="description" type="java.lang.String" -->

<#include "../template.ftl">

<@styles>
    <#macro styles>
        <#nested/>
    </#macro>
</@styles>

<@content>
    ${description}

    <div>
        <button type="button" onclick="showLogin()">Login</button>
    </div>

</@content>

<@scripts>
    <#macro scripts>
        <#nested/>
    </#macro>
</@scripts>
