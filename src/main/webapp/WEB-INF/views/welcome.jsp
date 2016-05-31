<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Devoxx Watson Content Uploader</title>
	<link href="<c:url value='/static/css/bootstrap.css' />"  rel="stylesheet"/>
	<link href="<c:url value='/static/css/app.css' />" rel="stylesheet"/>
</head>
<body>
	<div class="form-container">
		<h1>Welcome to the Devoxx Watson Content Uploader</h1>
		
		Here you can upload an OGG audio file or a Voxxed article to the Devoxx Watson corpus.<br/><br/>
		
		<a href="<c:url value='/audioFileUploader' />">Audio</a> or <a href="<c:url value='/articleUploader' />">Article</a> Upload
	</div> 
</body>
</html>
