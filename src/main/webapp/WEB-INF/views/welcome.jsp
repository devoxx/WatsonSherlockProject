<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Devoxx Watson Audio Uploader</title>
	<link href="<c:url value='/static/css/bootstrap.css' />"  rel="stylesheet"/>
	<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"/>
</head>
<body>
	<div class="form-container">
		<h1>Welcome to the Devoxx Watson Audio File Uploader</h1>
		
		Create an ogg audio file and upload it to the Devoxx Watson repository.<br/><br/>
		
		<a href="<c:url value='/audioFileUploader' />">Audio Upload</a>
	</div> 
</body>
</html>
