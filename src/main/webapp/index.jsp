<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="">
  <meta name="author" content="Jakub Stejskal">

  <title>NaLIDa - a natural language interface for database</title>

  <!-- Bootstrap core CSS -->
  <link href="bootstrap.min.css" rel="stylesheet">

  <!-- Custom styles for this template -->
  <link href="starter-template.css" rel="stylesheet">

  <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
      <![endif]-->
    </head>

    <body>

      <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container">
          <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">NaLIDa</a>
          </div>
          <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
              <li class="active"><a href="#">Home</a></li>
              <li><a href="#about">About</a></li>
              <li><a href="https://github.com/jakub-stejskal/nalida-web">GitHub</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>

      <div class="container">

        <div class="starter-template">
        <div class="page-header">
          <h1>Welcome to NaLIDa <small>a Natural Language Interface for Database</small></h1>
          </div>
          <p>Submit a natural language question in English. Use either a wh-question or noun phrase.</p>

            <div class="row">
                <div class="col-md-4">
            <h3>KOSapi response</h3>
            <form name="input" action="http://localhost:8080/nalida-web/api/kos" method="get">
            <div class="input-group">
              <input class="form-control" type="text" name="q">
              <span class="input-group-btn">
                <input class="btn btn-default" type="submit" value="Submit">
              </span>
            </div>
            </form>
            <h4>Examples</h4>
            <div class="list-group">
              <a class="list-group-item" href="http://localhost:8080/nalida-web/api/kos/?q=What%20is%20phone%20of%20Jan%20%C5%A0ediv%C3%BD?">What is phone of Jan Šedivý?</a>
              <a class="list-group-item" href="http://localhost:8080/nalida-web/api/kos/?q=Which%20teachers%20are%20from%20division%2013133?">Which teachers are from division 13133?</a>
            </div>
            </div>

            <div class="col-md-4">
            <h3>SQL query</h3>
            <form name="input" action="http://localhost:8080/nalida-web/api/kos/sql" method="get">
            <div class="input-group">
		      <input class="form-control" type="text" name="q">
		      <span class="input-group-btn">
		        <input class="btn btn-default" type="submit" value="Submit">
		      </span>
		    </div>
            </form>
            <h4>Examples</h4>
            <div class="list-group">
              <a class="list-group-item" href="http://localhost:8080/nalida-web/api/kos/sql?q=What%20is%20phone%20of%20Jan%20%C5%A0ediv%C3%BD?">What is phone of Jan Šedivý?</a>
              <a class="list-group-item" href="http://localhost:8080/nalida-web/api/kos/sql?q=Which%20teachers%20are%20from%20division%2013133?">Which teachers are from division 13133?</a>
            </div >
            </div>

            <div class="col-md-4">
            <h3>Detailed intermediate outputs</h3>
            <form name="input" action="http://localhost:8080/nalida-web/api/kos/debug" method="get">
            <div class="input-group">
              <input class="form-control" type="text" name="q">
              <span class="input-group-btn">
                <input class="btn btn-default" type="submit" value="Submit">
              </span>
            </div>
            </form>
            <h4>Examples</h4>
            <div class="list-group">
              <a class="list-group-item" href="http://localhost:8080/nalida-web/api/kos/debug?q=What%20is%20phone%20of%20Jan%20%C5%A0ediv%C3%BD?">What is phone of Jan Šedivý?</a>
              <a class="list-group-item" href="http://localhost:8080/nalida-web/api/kos/debug?q=Which%20teachers%20are%20from%20division%2013133?">Which teachers are from division 13133?</a>
            </div>
            </div>
          </div>
          </div>
        </div><!-- /.container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="bootstrap.min.js"></script>
  </body>
  </html>
