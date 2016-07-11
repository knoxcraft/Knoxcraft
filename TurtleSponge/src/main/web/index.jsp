<%-- 
<%@ taglib uri="/WEB-INF/tlds/kct.tld" prefix="kct" %>
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html><head>
<%-- <kct:headStuff title="Welcome to KnoxCraft Turtles"></kct:headStuff> --%>
<title>KnoxCraft Turtles</title>
</head>
<body>
<h1>Welcome to KnoxCraft Turtles!
</h1>
<p>
   Today's date: <%= (new java.util.Date()).toLocaleString() %>
</p>
<p>
<h2>
<a href="kctupload.jsp"> Upload KnoxCraft Turtles code through the web</a>
</h2>
</p>
<p>
<h2>
<a href="blocks.jsp"> Blockly editor </a>
</h2>
</p>
</body> 
</html>