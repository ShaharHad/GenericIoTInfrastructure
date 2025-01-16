<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Error</title>
        <link rel="stylesheet" href="../css/GeneralStyle.css">
        <link rel="stylesheet" href="../css/FailureMessage.css">
    </head>
    <body>
        <header>
            <ul>
                <li><a href="/">Home</a></li>
            </ul>
        </header>
        <div id="message">
            <h1 id="title"></h1>
        </div>
    </body>

    <script>
        <%
            String jsonString = (String) request.getAttribute("jsonString");
        %>
        const jsonData = '<%=jsonString%>';
        const jsonObject = JSON.parse(jsonData);

        document.getElementById("title").innerHTML = jsonObject.title;

    </script>
</html>
