<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="count" type="java.lang.Integer" -->

<div class="buttons">
<#if index != 0>
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="16" viewBox="-0.51 -0.479 24 16">
        <path class="button button-up" index="${index}" fill="transparent" stroke="#A5A5A5"
              stroke-width="4" stroke-miterlimit="10" d="M21.49 12.52l-10-10-10 10"></path>
    </svg>
</#if>
<#if index != (count - 1)>
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="16" viewBox="-0.475 -0.477 24 16">
        <path class="button button-down" index="${index}" fill="transparent" stroke="#A5A5A5"
              stroke-width="4" stroke-miterlimit="10" d="M1.525 1.523l10 10 10-10"></path>
    </svg>
</#if>
</div>
