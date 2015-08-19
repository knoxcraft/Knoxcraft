<%@ taglib uri="/WEB-INF/tlds/kct.tld" prefix="kct" %>
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tlds/fn.tld" prefix="fn" %>

<html>
<%--<head><kct:headStuff title="Code Upload Form"></kct:headStuff></head> --%>
<head><title>KnoxCraft Turtles Upload Form</title>
</head>
<body>
<h1>KnoxCraft Turtles 3D: Code Upload Form</h1>

<div>
<form method=POST action="/kctupload" enctype="multipart/form-data">

Player Name: <input type="text" name="playerName"><br>
<input type="hidden" name="client" value="web">
Language: <select name="language">
	<option value="Java" selected> Java </option>
	<option value="Python"> Python </option>
</select>
<p>
Source Code (paste here): <br><textarea rows=15 cols=60 name="sourcetext"></textarea><br>
JSON Turtle Commands (paste here): <br><textarea rows=15 cols=60 name="jsontext"></textarea><br>
Source Code (file upload): <input type="file" name="sourcefile"><br>
JSON Turtle Commands (file upload): <input type="file" name="jsonfile"><br>
<p>
<input type=submit value="upload"><br>
</p>
</p>
</form>
</div>
</body>
</html>
