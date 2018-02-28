<#-- @ftlvariable name="stepNode" type="org.stepik.core.courseFormat.stepHelpers.StepHelper" -->
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

    <style media="screen" type="text/css">
        body {
            font-size: ${font_size}pt !important;
            padding: 10px 15px;
        }

        #load_animation {
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
    </style>

    <#nested/>
</#macro>

</head>
<body>

<#macro content>
    <#nested/>
</#macro>

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

    var nodeList = document.body.getElementsByTagName("code");

    for (var i = 0; i < nodeList.length; i++) {
        var item = nodeList[i];
        item.innerHTML = item.innerHTML.replace(new RegExp("<br>", "g"), "\n");
    }

    //noinspection JSUnresolvedVariable
    hljs.initHighlighting();

    var action_element = document.getElementById("action");

    function login() {
        action_element.setAttribute("value", "login");
    }

    function next_step() {
        action_element.setAttribute("value", "next_step");
    }

</script>
    <#nested/>
</#macro>
</body>
</html>
