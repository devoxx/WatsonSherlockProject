<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Devoxx Watson Content Upload Success</title>
	<link href="<c:url value='/static/css/bootstrap.css' />" rel="stylesheet"/>
	<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"/>
</head>
<body>
	<div class="success">
		<strong>${content}</strong> uploaded successfully.
		<br/><br/>
		IBM Watson is now processing your content...
		<br/><br/>
		<a href="<c:url value='/welcome' />">Home</a>	
	</div>
</body>
</html>