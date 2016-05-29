<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Audio File Upload Success</title>
	<link href="<c:url value='/static/css/bootstrap.css' />" rel="stylesheet"></link>
	<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
</head>
<body>
	<div class="success">
		File  <strong>${fileName}</strong> uploaded successfully.
		<br/><br/>
		IBM Watson is now processing your audio file...
		<br/><br/>
		<a href="<c:url value='/welcome' />">Home</a>	
	</div>
</body>
</html>