<%@ taglib uri="/WEB-INF/tlds/kct.tld" prefix="kct" %> 
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tlds/fn.tld" prefix="fn" %>

<html>
<head>
<%--
For some reason I cannot use any of my own tags! I get an exception every time
I try to use kct:headStuff. Even if headStuff.tag is empty, it still fails. The
error is about Map$Entry not being found, which the internet says would be OK
using Java 7 but not Java 8. Can't seem to change my environment to make it work, though.

I will probably replace the jsps with grails anyway, even if it's huge.

<kct:headStuff title="Admin Panel"></kct:headStuff>
 --%>
 
 
<title>Admin Panel</title>
<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/kct.css"></link>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
 
</head>
<body>
<h1>Instructor View</h1>

<p>
<a href="/admin/download"> Download all submissions </a>
</p>

<table border=1>
<c:forEach var="thing" items="${scripts}">
<tr>
<td>${thing.value.playerName} </td>
<td>${thing.value.scriptName} </td>
<td>${thing.value.language} </td>
<td>${thing.value.sourceCode} </td>
<td></td>
</tr>
</c:forEach>

</table>

</body>
</html>