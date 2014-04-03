<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
<body>
<h2>Welcome to NaLIDA</h2>
<p>Enter you natural language question (in English):
	<form name="input" action="http://localhost:8080/nalida-web/api/" method="get">
	<label><input type="text" name="q"></label>	 
	<input type="submit" value="Submit">
	</form>
	<h3>KOSapi response</h3>
	<ul>
		<li><a href="http://localhost:8080/nalida-web/api/kos/?q=What%20is%20phone%20of%20Jan%20%C5%A0ediv%C3%BD?">What is phone of Jan Šedivý?</a>
		<li><a href="http://localhost:8080/nalida-web/api/kos/?q=Which%20teachers%20are%20from%20division%2013133?">Which teachers are from division 13133?</a>
	</ul>
	<h3>SQL query</h3>
	<ul>
		<li><a href="http://localhost:8080/nalida-web/api/kos/sql?q=What%20is%20phone%20of%20Jan%20%C5%A0ediv%C3%BD?">What is phone of Jan Šedivý?</a>
		<li><a href="http://localhost:8080/nalida-web/api/kos/sql?q=Which%20teachers%20are%20from%20division%2013133?">Which teachers are from division 13133?</a>
	</ul>
	<h3>Debug output</h3>
	<ul>
		<li><a href="http://localhost:8080/nalida-web/api/kos/debug?q=What%20is%20phone%20of%20Jan%20%C5%A0ediv%C3%BD?">What is phone of Jan Šedivý?</a>
		<li><a href="http://localhost:8080/nalida-web/api/kos/debug?q=Which%20teachers%20are%20from%20division%2013133?">Which teachers are from division 13133?</a>
	</ul>
</body>
</html>