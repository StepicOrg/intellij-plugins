<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.StepHelper" -->
<#-- @ftlvariable name="login_css" type="java.lang.String" -->
<#-- @ftlvariable name="content" type="java.lang.String" -->
<#-- @ftlvariable name="loader" type="java.lang.String" -->
<#-- @ftlvariable name="mathjax" type="java.lang.String" -->
<#-- @ftlvariable name="charset" type="java.lang.String" -->
<#-- @ftlvariable name="font_size" type="java.lang.Number" -->
<#-- @ftlvariable name="highlight" type="java.lang.String" -->
<#-- @ftlvariable name="css_highlight" type="java.lang.String" -->

<!DOCTYPE html>
<html>
<head>
    <meta charset="${charset}">

<#macro styles>
    <link rel="stylesheet" href="${css_highlight}">
    <link rel="stylesheet" href="${login_css}">

    <style media="screen" type="text/css">
        body {
            font-size: ${font_size}pt !important;
            padding: 10px 15px;
        }

        #load_animation, #login {
            display: none;
            position: absolute;
            width: 100%;
            height: 100%;
            left: 0;
            top: 0;
            background-color: rgba(0, 0, 0, 0.3);
        }

        #load_animation object {
            margin: auto;
        }

        #close {
            text-align: right;
        }

        #close:hover {
            text-decoration: underline;
            cursor: pointer;
        }

        #errors {
            color: red;
        }
    </style>

    <#nested/>
</#macro>

<#if content?has_content>
${content}
</#if>

</head>
<body>

<#macro content>
    <#nested/>
</#macro>

<div id="login">
    <form id="login-form" action="<#if stepNode??>${stepNode.getPath()}</#if>" method="post">
        <div id="close" onclick="hideLogin()">Close</div>
        <h2>Login</h2>
        <p id="errors"></p>
        <label for="email"> Email:</label>
        <input id="email" type="email" name="email" value="">
        <label for="password"> Password:</label>
        <input id="password" type="password" name="password" value="">
        <input type="submit" value="Login" onclick="checkValues()">
        <input type="hidden" name="action" value="login">
    </form>
</div>

<div id="load_animation">
    <object type="image/svg+xml" data="${loader}" class='icon'></object>
</div>

<#macro scripts>
<script src="${highlight}"></script>

<script type="text/x-mathjax-config">
    MathJax.Hub.Config({
      tex2jax: {inlineMath: [['$','$']], preview: "none"},
      TeX: {extensions: ["mhchem.js", "color.js"]},
      messageStyle: "none",
    });

</script>

<script type="text/javascript" async
        src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-AMS_CHTML">
</script>

<script>
    var load_animation = document.getElementById("load_animation");

    //noinspection JSUnusedLocalSymbols
    function showLoadAnimation() {
        load_animation.style.display = "flex";
    }

    //noinspection JSUnusedLocalSymbols
    function hideLoadAnimation() {
        load_animation.style.display = "none";
    }

    var login = document.getElementById("login");

    //noinspection JSUnusedLocalSymbols
    function showLogin() {
        login.style.display = "flex";
        console.warn("show login");
    }

    //noinspection JSUnusedLocalSymbols
    function hideLogin() {
        login.style.display = "none";
    }

    var nodeList = document.body.getElementsByTagName("code");

    for (var i = 0; i < nodeList.length; i++) {
        var item = nodeList[i];
        item.innerHTML = item.innerHTML.replace(new RegExp("<br>", "g"), "\n");
    }

    //noinspection JSUnresolvedVariable
    hljs.initHighlighting();

    function checkValues() {

    }

    var errors = document.body.getElementById("errors");

    //noinspection JSUnusedLocalSymbols
    function setErrorMessage(message) {
        errors.innerText = message;
    }
</script>
    <#nested/>
</#macro>
</body>
</html>
