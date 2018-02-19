<#-- @ftlvariable name="url" type="java.lang.String" -->
<!DOCTYPE html>
<html>
<head>
    <title>Error</title>

    <style>
        body {
            padding: 15px;
            background-color: #fbfbfb;
        }

        #try {
            display: inline-block;
            padding: 10px;
            border: 1px solid lightblue;
            border-radius: 5px;
            text-decoration: none;
            background-color: blue;
            color: white;
        }

        #try:hover {
            background-color: darkblue;
        }

        #try:active {
            background-color: blue;
        }

        .list {
            padding: 15px;
        }
    </style>
</head>
<body>
<h1>Connection failed</h1>
<hr>
<ul class="list">
    <li>Check internet connection options</li>
</ul>

<a id="try" href="${url}">Try again</a>
</body>
</html>