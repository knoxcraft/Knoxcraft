
<%@ taglib uri="/WEB-INF/tlds/c.tld" prefix="c" %>
<%@ attribute name="title" required="false" %>
<title>KnoxCraft Turtles<c:if test="${! empty title}"> - ${title}</c:if></title>

<link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/css/kct.css"></link>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
