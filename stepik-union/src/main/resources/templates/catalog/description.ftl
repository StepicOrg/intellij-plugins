<#-- @ftlvariable name="coverUrl" type="java.lang.String" -->
<#-- @ftlvariable name="studyObject" type="org.stepik.api.objects.StudyObject" -->
<#-- @ftlvariable name="summary" type="java.lang.String" -->
<#-- @ftlvariable name="updateDate" type="java.lang.String" -->

<!DOCTYPE html>

<html>
<meta charset="UTF-8">
<body>
<div>
<#if studyObject.isAdaptive()><b>Adaptive course</b><br></#if>
    Updated: ${updateDate}
</div>
<div>
${studyObject.description}
</div>
<p>
${summary}
</p>
</body>
</html>