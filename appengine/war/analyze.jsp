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
	    <link href='http://fonts.googleapis.com/css?family=Roboto+Slab' rel='stylesheet' type='text/css'>
	
	    <header class="navbar navbar-default" role="navigation">
	        <div class="container">
	            <div class="navbar-brand">
	                <a href="@routes.Application.index">Sentiment Analysis <small>- Company <%= request.getAttribute( "name" ) %></small></a>
	            </div>
	            <ul class="nav navbar-nav navbar-right">
	                <li><a href="/logout">Abmelden</a></li>
	            </ul>
	        </div>
	    </header>
	
	    <div class="container" id="content">
	        <button type="button" data-loading-text="Analyzing..." class="btn btn-primary btn-lg" id="btn-analyze">
	            Start analyzing
	        </button>
	
	        <p id="result" style="display: none">
	            0.3
	        </p>
	    </div>
	
	    <script type="text/javascript" charset="utf-8" src="/javascripts/analyze.js"></script>
    </body>
</html>
