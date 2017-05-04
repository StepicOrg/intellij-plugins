<#-- @ftlvariable name="mathjax" type="java.lang.String" -->
<#-- @ftlvariable name="charset" type="java.lang.String" -->
<#-- @ftlvariable name="code" type="java.lang.String" -->
<#-- @ftlvariable name="font_size" type="java.lang.Number" -->
<#-- @ftlvariable name="highlight" type="java.lang.String" -->
<#-- @ftlvariable name="css_highlight" type="java.lang.String" -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="${charset}">
    <link rel="stylesheet" href="${css_highlight}">
    <script src="${highlight}"></script>
    <style media="screen" type="text/css">
        body {
            font-size: ${font_size}pt !important;
            padding: 10px 15px;
        }
    </style>

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
</head>
<body>
${code}
<script>
    var nodeList = document.body.getElementsByTagName("code");

    for (var i = 0; i < nodeList.length; i++) {
        var item = nodeList[i];
        item.innerHTML = item.innerHTML.replace(new RegExp("<br>", "g"), "\n");
    }

    //noinspection JSUnresolvedVariable
    hljs.initHighlighting()
</script>
</body>
</html>