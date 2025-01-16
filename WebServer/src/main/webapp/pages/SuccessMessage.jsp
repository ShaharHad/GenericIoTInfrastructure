<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Success</title>
        <link rel="stylesheet" href="../css/GeneralStyle.css">
        <link rel="stylesheet" href="../css/SuccessMessage.css">
    </head>
    <body>
        <header>
            <ul>
                <li><a href="/">Home</a></li>
            </ul>
        </header>
        <div class="main_title">
            <h1 id="title"></h1>
        </div>
        <div class="message_container" id="message_container">
            <h1 id="message"></h1>
        </div>
    </body>
    <script>
        <%
            String jsonString = (String) request.getAttribute("jsonString");
        %>
        const jsonData = '<%=jsonString%>';

        const jsonObject = JSON.parse(jsonData);

        document.getElementById("title").innerHTML = jsonObject.title;

        if(null != jsonObject.message){
            document.getElementById("message_container").className = 'message_found'
            document.getElementById("message").innerHTML = jsonObject.message;
        }

    </script>
</html>
