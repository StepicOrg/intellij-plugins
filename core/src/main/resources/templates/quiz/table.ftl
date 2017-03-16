<#-- @ftlvariable name="stepNode" type="com.jetbrains.tmp.learning.courseFormat.stepHelpers.TableStepNodeHelper" -->
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
</style>

<#include "base.ftl">

<@quiz_content>

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
</@quiz_content>