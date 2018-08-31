<#-- @ftlvariable name="link" type="java.lang.String" -->
<#-- @ftlvariable name="error" type="java.lang.String" -->

<!DOCTYPE html>

<html>
<head>
    <title>Authorization on ${pluginName}</title>
    <meta charset="utf-8">

    <style>
        body {
            font-size: 24pt;
            padding: 10px 15px;
            text-align: center;
        }

        h1 {
            color: darkslategray;
        }
    </style>
</head>
<body>
<h1>Authorization on ${pluginName} is failed</h1>
<p>${error}</p>

<a href="${link}">Retry</a>
</body>
</html>
