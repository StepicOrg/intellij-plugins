<#-- @ftlvariable name="action" type="java.lang.String" -->
<#-- @ftlvariable name="disabled" type="java.lang.String" -->
<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.TableQuizHelper" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<style>
    .table {
        width: 100%;
        border-collapse: collapse;
    }

    .table td {
        text-align: center;
    }

    .table th:first-child,
    .table td:first-child {
        text-align: left;
    }

    .table td,
    .table th {
        border-bottom: 1px solid darkgray;
    }
</style>

<#include "base.ftl">

<@quiz_content>
    <#if action != "get_first_attempt" && action != "need_login" >
    <table class="table">
        <tr>
            <th><b>${stepNode.getDescription()}</b></th>
            <#list stepNode.getColumns() as column>
                <th>${column}</th>
            </#list>
        </tr>

        <#assign type=stepNode.isCheckbox()?string("checkbox", "radio") />
        <#assign row_index = 0/>
        <#list stepNode.getRows() as row>
            <tr>
                <td>${row}</td>

                <#assign column_index = 0/>
                <#list stepNode.getColumns() as column>
                    <td>
                        <input type="${type}" name="${row}"
                               value="${column}" ${disabled!""}
                               title="${row}:${column}" ${stepNode.getChoice(row, column)?string("checked", "")}/>
                    </td>
                    <#assign column_index += 1 />
                </#list>
                <#assign row_index += 1 />
            </tr>
        </#list>
    </table>
    </#if>
</@quiz_content>