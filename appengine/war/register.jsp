<!DOCTYPE html>

<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Sentiment Analysis</title>

        <link rel="stylesheet" media="screen" href="/bootstrap/css/bootstrap.css">
        <link rel="stylesheet/less" type="text/css" href="/stylesheets/main.less" />
        <link rel="shortcut icon" type="image/png" href="/images/favicon.ico">

        <script type="text/javascript" src="/javascripts/jquery-1.9.0.min.js"></script>
        <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
        <script src="javascripts/less-1.6.1.min.js" type="text/javascript"></script>
    </head>
    <body>
<header class="navbar navbar-default" role="navigation">
    <div class="container">
        <div class="navbar-brand">
            <a href="/">Sentiment Analysis</a>
        </div>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="/">Back</a></li>
        </ul>
    </div>
</header>

<div class="container">
    <form action="/register" method="POST">
        <h1>New Company</h1>

        <% if( request.getAttribute( "message" ) != null ) { %>
            <p class="error">
                <%= request.getAttribute( "message" ) %>
            </p>
        <% } %>

        <p>
        	<% String n = (String)request.getAttribute( "name" ); %>
            <input class="form-control" type="text" name="name" placeholder="Company" value="<%= n != null ? n : "" %>">
        </p>
        <p>
            <input class="form-control" type="password" name="password1" placeholder="Password">
        </p>
        <p>
            <input class="form-control" type="password" name="password2" placeholder="Re-enter Password">
        </p>
        <p>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
        </p>
    </form>
</div>
    </body>
</html>